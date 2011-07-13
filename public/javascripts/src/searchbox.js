/*jshint sub: true, strict: true */
/*global jQuery: false, window: false */

(function($){

"use strict";

var searchTimeout = null;

$.fn["searchbox"] = function(w) {

    var box = this;

    function applySearch(delay) {
        if (searchTimeout) {
            window.clearTimeout(searchTimeout);
        }
        searchTimeout = window.setTimeout(function(){
            var q = $(box).val();
            if (q.length < 3) {
                window.location.hash = "#" + w.box;
                return;
            }
            window.location.hash = "#" + w.box + "/" + q;
        }, delay ? 300 : 0);
    }

    $(this).keyup(function(){
        applySearch(true);
    });

    $(this).click(function(){
        if ($(this).val() === "") {
            applySearch();
        }
    });
};

})(jQuery);
