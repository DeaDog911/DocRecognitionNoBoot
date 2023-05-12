function popupActive(el){
    const popup = document.getElementById('popup');
    popup.classList.add('active');
    const documentId = el.parentElement.parentElement.id;
    popup.getElementsByTagName("form")[0].elements.id.value = documentId;
}
function popupPassive(){
    const popup = document.getElementById('popup');
    popup.classList.remove('active');
}

function checkboxActive(el){
    const popup = document.getElementById('check_box');
    popup.classList.add('active');
}
function checkboxPassive(){
    const popup = document.getElementById('check_box');
    popup.classList.remove('active');
}
function uploadDocument(el) {
    let ext = el.value.match(/\.([^\.]+)$/)[1];
    if (ext=='pdf'){
        const name= prompt("Введите имя файла");
        const author = prompt("Введите имя автора");
        let uploadForm = document.getElementById('upload');
        if (name !== "" && name !== null && author !== "" && author != null) {
            uploadForm.elements.name.value = name;
            uploadForm.elements.author.value = author;
            if(document.getElementById('fid-1').checked) {
                uploadForm.elements.language.value = 'rus';
            }else if(document.getElementById('fid-2').checked) {
                uploadForm.elements.language.value = 'eng';
            }
            uploadForm.submit();
        }else {
            alert("Имя файла или имя автора не может быть пуcтой строкой");
            window.location.reload();
        }
    }
    else{
        alert('Файл не может быть обработан из-за неверного формата');
        el.value = '';
    }
}
function updateDocument(el) {
    const documentId = el.parentElement.parentElement.id;
    window.location = "update?id="+documentId;
}
function textActive(text){
    const text_popup = document.getElementById('text_popup');
    var p =  text_popup.children[0];
    p.innerHTML = text;
    text_popup.classList.add('active');
}

// $('a').each(function() {
//     if ($(this).text().length > 40) {
//         $(this).text( $(this).text().substring(0, 40) + '…');
//     }
// });

function textPassive(){
    const text_popup = document.getElementById('text_popup');
    text_popup.classList.remove('active');
}
function activePage(e) {
    var elems = document.querySelectorAll(".active");
    [].forEach.call(elems, function(el) {
        el.classList.remove("active");
        el.classList.add("inactive");
    });
    e.classList.remove('inactive');
    e.classList.add('active');
}
function show(shown, hidden) {
    document.getElementById(shown).style.display='block';
    document.getElementById(hidden).style.display='none';
    return false;
}

function cancelMainPage() {
    window.location = window.location.href.split("/").slice(0,-1).join("/") + "/";
}

function search_id(e){
    var id = e.value;
    var parent_e = e.parentElement;
    parent_e.children[1].href="?id="+id;
}

addPagerToTables('#mainTable', 5);

var search_btn = document.getElementsByClassName('search-btn')[0];
var searchBox = document.getElementsByClassName('search-box')[0];
searchBox.addEventListener("keyup", function (event) {
    if (event.keyCode === 13) {
        search_btn.click();
    }
});

function addPagerToTables(tables, rowsPerPage = 10) {
    tables =
        typeof tables == "string"
            ? document.querySelectorAll(tables)
            : tables;

    for (let table of tables)
        addPagerToTable(table, rowsPerPage);
}

function addPagerToTable(table, rowsPerPage = 10) {
    let tBodyRows = getBodyRows(table);
    let numPages = Math.ceil(tBodyRows.length/rowsPerPage);

    let colCount =
        [].slice.call(
            table.querySelector('tr').cells
        )
            .reduce((a,b) => a + parseInt(b.colSpan), 0);

    table
        .createTFoot()
        .insertRow()
        .innerHTML = `<td colspan=${colCount}><div class="nav" id="nav"></div></td>`;

    if(numPages == 1)
        return;

    for(i = 0;i < numPages;i++) {

        let pageNum = i + 1;

        table.querySelector('.nav')
            .insertAdjacentHTML(
                'beforeend',
                `<a href="#" rel="${i}">${pageNum}</a> `
            );

    }

    changeToPage(table, 1, rowsPerPage);

    for (let navA of table.querySelectorAll('.nav a'))
        navA.addEventListener(
            'click',
            e => changeToPage(
                table,
                parseInt(e.target.innerHTML),
                rowsPerPage
            )
        );

}

function changeToPage(table, page, rowsPerPage) {

    let startItem = (page - 1) * rowsPerPage;
    let endItem = startItem + rowsPerPage;
    let navAs = table.querySelectorAll('.nav a');
    let tBodyRows = getBodyRows(table);

    for (let nix = 0; nix < navAs.length; nix++) {

        if (nix == page - 1)
            navAs[nix].classList.add('active');
        else
            navAs[nix].classList.remove('active');

        for (let trix = 0; trix < tBodyRows.length; trix++)
            tBodyRows[trix].style.display =
                (trix >= startItem && trix < endItem)
                    ? 'table-row'
                    : 'none';

    }
}
function getBodyRows(table) {
    let initial = table.querySelectorAll('tbody tr');
    return Array.from(initial)
        .filter(row => row.querySelectorAll('td').length > 0);
}


