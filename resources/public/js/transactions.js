$(document).ready(function(){
    var transactions = {
        attachHandlers: function() {
            $('select').change(function() {
                transactions.updateTransaction(this.value, this.id);
            });
        },
        updateTransaction: function(categoryId, elementId) {
            var pieces = elementId.split("-");
            var transactionId = pieces[2];
            $.ajax({
                method: "PUT",
                contentType: "application/json",
                url: "/transactions/" + transactionId,
                data: JSON.stringify({ category_id: categoryId }),
            })
            .error(function (jqXHR, exception) {
                handleError(jqXHR, exception);
            }).
            complete(function () {
                $( "#" + elementId ).delay(100).fadeOut().fadeIn('slow');
            });
        },
        handleError: function(jqXHR, exception) {
            var msg = '';
            if (jqXHR.status === 0) {
                msg = 'Not connect.\n Verify Network.';
            } else if (jqXHR.status == 404) {
                msg = 'Requested page not found. [404]';
            } else if (jqXHR.status == 500) {
                msg = 'Internal Server Error [500].';
            } else if (exception === 'parsererror') {
                msg = 'Requested JSON parse failed.';
            } else if (exception === 'timeout') {
                msg = 'Time out error.';
            } else if (exception === 'abort') {
                msg = 'Ajax request aborted.';
            } else {
                msg = 'Uncaught Error.\n' + jqXHR.responseText;
            }
            console.log(msg);
        }
    };
    transactions.attachHandlers();
});
