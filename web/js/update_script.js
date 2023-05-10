var file = document.getElementById('fileId');

file.onchange = function(e) {
    var ext = this.value.match(/\.([^\.]+)$/)[1];
    if (ext=='pdf'){
        alert('Документ успешно отправлен на сервер');
    }
    else{
        alert('Файл не может быть обработан из-за неверного формата');
        this.value = '';
    }
}

var author;
var filename;

function editData(){
    document.getElementById("update").submit();
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