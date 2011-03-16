(function($){

var Notification = function(elt) {
    this.elt = $(elt);
    this.notif = $("<span></span>").addClass("notification").hide();
    this.elt.append(this.notif);
    this.timer = null;
}

Notification.prototype = {

    /* Show a message during a given delay (in ms) .
     * Pass null to leave the message indefinitely.
     */
    display: function(message, delay) {
        if (this.timer) {
            window.clearTimeout(this.timer);
        }
        this.notif.text(message);
        this.notif.show();
        var notif = this.notif;
        if (delay && delay > 0) {
            this.timer = window.setTimeout(function(){
                notif.hide();
            }, delay);
        }
    }

};

window.Notification = Notification;

})(jQuery);