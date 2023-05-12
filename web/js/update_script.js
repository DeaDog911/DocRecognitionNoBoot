var file = document.getElementById('fileId');

file.onchange = function(e) {
    var ext = this.value.match(/\.([^\.]+)$/)[1];
    if (ext=='pdf'){
    }
    else{
        alert('Файл не может быть обработан из-за неверного формата');
        this.value = '';
    }
}

var author;
var filename;

function editData(){
    let updateForm = document.getElementById("update");
    if(document.getElementById('fid-1').checked) {
        updateForm.elements.language.value = 'rus';
    }else if(document.getElementById('fid-2').checked) {
        updateForm.elements.language.value = 'eng';
    }
    updateForm.submit();
}

function sendAuthor(e){
    author = e.value;
}

function cancelUpdate() {
    window.location = window.location.href.split("/").slice(0,-1).join("/") + "/";
}
function sendFilename(e){
    filename= e.value;
}