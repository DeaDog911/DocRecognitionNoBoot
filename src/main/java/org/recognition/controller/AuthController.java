package org.recognition.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.recognition.entity.UserEntity;
import org.recognition.services.SecurityService;
import org.recognition.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.Locale;
import java.util.UUID;

@Controller
public class AuthController implements WebMvcConfigurer {
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private Environment env;
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
    @GetMapping("/registration")
    public String registration(Model model) {
        return "registration";
    }
    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") UserEntity userForm, Model model) {
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            System.out.println(userForm.getPassword() + " " + userForm.getPasswordConfirm());
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        if (!userService.saveUser(userForm)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }
        return "redirect:/";
    }

    @GetMapping("/reset_password")
    public String reset_password(Authentication authentication, @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer, Model model) {
        if(referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }
        if (authentication != null)
            return "redirect:/new_password";
        else
            return "reset_password";
    }

    @PostMapping("/reset_password")
    @ResponseBody
    public String reset_password(HttpServletRequest request,
                                 @RequestParam("username") String username,
                                 @RequestParam("email") String email) throws MessagingException {
        UserEntity user = userService.loadUserByUsername(username);
        if (user == null || !user.getEmail().equals(email)) {
            throw new UsernameNotFoundException("User not found with username or email");
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        mailSender.send(constructResetTokenEmail(request.getRequestURL().toString(), token, user));
        return "<html lang=ru>\n" + "<header><title>Reset Password</title></header>\n" +
                "<body>\n" + "Check your mail\n" + "</body>\n" + "</html>";
    }

    @GetMapping(value = "/reset_password", params = {"token"})
    public String show_reset_password_page(Authentication authentication, @RequestParam(name = "token", required = false) String token) {
        if (authentication != null) {
            return "redirect:/new_password";
        }
        String result = securityService.validatePasswordResetToken(token);
        if(result != null) {
            System.out.println(result);
            return "redirect:/login";
        } else {
            return "redirect:/new_password?token=" + token;
        }
    }

    @GetMapping(value = "/new_password")
    public String new_password(@RequestParam(name = "token", required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "new_password";
    }

    @PostMapping(value = "/new_password")
    public String save_new_password(@RequestParam("password") String password, @RequestParam("token") String token,
                                    Authentication authentication) {
        System.out.println(password);
        System.out.println(token);
        UserEntity user;
        if (authentication == null) {
            user = userService.getUserByPasswordResetToken(token);
            if (user != null) {
                userService.changeUserPassword(user, password);
                return "redirect:/login";
            }else {
                throw new UsernameNotFoundException("User not found");
            }
        }else {
            user = userService.loadUserByUsername(authentication.getName());
            userService.changeUserPassword(user, password);
            return "redirect:/";
        }
    }

    private MimeMessage constructResetTokenEmail(
            String contextPath, String token, UserEntity user) throws MessagingException {
        String url = contextPath + "?token=" + token;
        String body = createMailBody(url);
        return constructEmail("Reset Password", body, user);
    }

    private MimeMessage constructEmail(String subject, String body,
                                             UserEntity user) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setTo(user.getEmail());
        helper.setFrom(env.getProperty("mail_sender_username"));
        return mimeMessage;
    }

    private String createMailBody(String token) {
        return
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"width:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"><head><meta charset=\"UTF-8\"><meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"><meta name=\"x-apple-disable-message-reformatting\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta content=\"telephone=no\" name=\"format-detection\"><title>Новый шаблон 2023-05-12</title><!--[if (mso 16)]><style type=\"text/css\">     a {text-decoration: none;}     </style><![endif]--><!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--><!--[if gte mso 9]><xml> <o:OfficeDocumentSettings> <o:AllowPNG></o:AllowPNG> <o:PixelsPerInch>96</o:PixelsPerInch> </o:OfficeDocumentSettings> </xml><![endif]--><!--[if !mso]><!-- --><link href=\"https://fonts.googleapis.com/css?family=Lato:400,400i,700,700i\" rel=\"stylesheet\"><!--<![endif]--><style type=\"text/css\">#outlook a {\tpadding:0;}.ExternalClass {\twidth:100%;}.ExternalClass,.ExternalClass p,.ExternalClass span,.ExternalClass font,.ExternalClass td,.ExternalClass div {\tline-height:100%;}.es-button {\tmso-style-priority:100!important;\ttext-decoration:none!important;}a[x-apple-data-detectors] {\tcolor:inherit!important;\ttext-decoration:none!important;\tfont-size:inherit!important;\tfont-family:inherit!important;\tfont-weight:inherit!important;\tline-height:inherit!important;}.es-desk-hidden {\tdisplay:none;\tfloat:left;\toverflow:hidden;\twidth:0;\tmax-height:0;\tline-height:0;\tmso-hide:all;}@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:30px!important; text-align:center } h2 { font-size:26px!important; text-align:center } h3 { font-size:20px!important; text-align:center } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:30px!important } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:26px!important } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:16px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button, button.es-button { font-size:20px!important; display:block!important; padding:15px 25px 15px 25px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; max-height:inherit!important } }</style></head>\n" +
                "<body bis_status=\"ok\" bis_frame_id=\"252\" style=\"width:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;padding:0;Margin:0\"><div class=\"es-wrapper-color\" style=\"background-color:#F4F4F4\"><!--[if gte mso 9]><v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\"> <v:fill type=\"tile\" color=\"#f4f4f4\"></v:fill> </v:background><![endif]--><table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#F4F4F4\"><tr class=\"gmail-fix\" height=\"0\" style=\"border-collapse:collapse\"><td style=\"padding:0;Margin:0\"><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:600px\"><tr style=\"border-collapse:collapse\"><td cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"padding:0;Margin:0;line-height:1px;min-width:600px\" height=\"0\"><img src=\"https://mgudpl.stripocdn.email/content/guids/CABINET_837dc1d79e3a5eca5eb1609bfe9fd374/images/41521605538834349.png\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;max-height:0px;min-height:0px;min-width:600px;width:600px\" alt width=\"600\" height=\"1\"></td>\n" +
                "</tr></table></td>\n" +
                "</tr><tr style=\"border-collapse:collapse\"><td valign=\"top\" style=\"padding:0;Margin:0\"><table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#7C72DC;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td style=\"padding:0;Margin:0;background-color:#6fa8dc\" bgcolor=\"#6fa8dc\" align=\"center\"><table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" bgcolor=\"#6fa8dc\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#6fa8dc;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-bottom:10px;padding-left:10px;padding-right:10px;padding-top:20px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:580px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"Margin:0;padding-bottom:5px;padding-left:30px;padding-right:30px;padding-top:35px\"><h1 style=\"Margin:0;line-height:58px;mso-line-height-rule:exactly;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;font-size:48px;font-style:normal;font-weight:normal;color:#111111\">Забыли пароль?</h1>\n" +
                "</td></tr></table></td></tr></table></td></tr></table></td>\n" +
                "</tr></table><table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td align=\"center\" bgcolor=\"#6fa8dc\" style=\"padding:0;Margin:0;background-color:#6fa8dc\"><table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#6fa8dc;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#6fa8dc\" align=\"center\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\"><table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#ffffff\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" role=\"presentation\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><img class=\"adapt-img\" src=\"https://mgudpl.stripocdn.email/content/guids/7cd57aa4-a023-41a0-a395-9598e0dfd315/images/catcare_1.jpg\" alt=\"Сбросить ваш пароль очень просто. Просто нажмите кнопку ниже, она переместит вас на страницу сброса пароля\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;font-size:12px\" width=\"600\" title=\"Сбросить ваш пароль очень просто. Просто нажмите кнопку ниже, она переместит вас на сайт для сброса пароля\"></td>\n" +
                "</tr><tr style=\"border-collapse:collapse\"><td class=\"es-m-txt-l\" bgcolor=\"#6fa8dc\" align=\"left\" style=\"Margin:0;padding-bottom:15px;padding-top:20px;padding-left:30px;padding-right:30px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;line-height:27px;color:#333333;font-size:18px\"><strong>Сбросить ваш пароль очень просто. Просто нажмите кнопку ниже, она переместит вас на страницу сброса пароля.Если пароль запросили не вы - ничего не делайте</strong><strong></strong></p></td></tr></table></td></tr></table></td>\n" +
                "</tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-bottom:20px;padding-left:30px;padding-right:30px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:540px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"Margin:0;padding-left:10px;padding-right:10px;padding-top:40px;padding-bottom:40px\"><span class=\"es-button-border\" style=\"border-style:solid;border-color:#7C72DC;background:#1d18be;border-width:1px;display:inline-block;border-radius:2px;width:auto;mso-border-alt:10px\">" +
                "<a href=\"" + token + "\" class=\"es-button\" target=\"_blank\" style=\"mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;color:#FFFFFF;font-size:20px;display:inline-block;background:#1d18be;border-radius:2px;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;font-weight:normal;font-style:normal;line-height:24px;width:auto;text-align:center;padding:15px 25px 15px 25px;border-color:#1d18be\">Сбросить пароль</a></span></td>\n" +
                "</tr></table></td></tr></table></td></tr></table></td></tr></table></td></tr></table></div></body></html>";
    }
}
