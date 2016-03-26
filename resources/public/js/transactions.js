$(document).ready(function(){
    var transactions = {
        attachHandlers: function() {
            console.log("fired 'attachHandlers'");
            $('select').change(function() {
                console.log("got " +  this.value + "for id " + this.id);
            });
        }
    };
    transactions.attachHandlers();
});
