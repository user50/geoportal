
window.keepAlive = window.keepAlive || {};
window.keepAlive.mesg = window.keepAlive.mesg || function(msg){
    $.showPageDialog("Предупреждение", msg, { "Перезагрузить страницу": function(){window.location.href='';}});
};

$(document).ready(function(){
    var srcFunc = arguments.callee;
    $.ajax({
        url: 'keepAlive',
        type: 'GET',
        success: function(data){
            if(data){
                setTimeout(srcFunc, 180000);
            }else{
                keepAlive.mesg("Ваша сессия истекла, необходимо перезагрузить страницу");
            }
        },
        error: function(data){
            keepAlive.mesg("Сервер не доступен, необходимо перезагрузить страницу");
        },
        // Form data
        data: "userName=" + getUserName(),
        //Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false
    });
});
