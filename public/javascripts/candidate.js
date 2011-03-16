
(function($){

"use strict";

var Candidate = function(resumeId, options) {
    this.resumeId = resumeId;
    this.options = $.extend({
        "allTags": [],
        "userTags": []
    }, options);
    if (this.options.post) {
        this.initInplaceEdit();
    }
    this.initTabBar();
    this.setTagBox();
};

Candidate.prototype = {
    initInplaceEdit: function(){
        var candidate = this;
        $(".inplace").each(function() {
            var self = this;
            $(this).editable(candidate.options.post, {
                tooltip: 'Click to edit',
                attribute: this.id,
                style: 'display: inline',
                submitdata : {resumeId: candidate.resumeId},
                width: self.id == "name" ? "auto" : 100,
                onsubmit: function() {
                    if (candidate.options.notification) {
                        candidate.options.notification.display("Saving...");
                    }
                },
                callback: function () {
                    if (candidate.options.notification) {
                        candidate.options.notification.display("Successfully saved", 2000);
                    }
                },
                onerror: function () {
                    if (candidate.options.notification) {
                        candidate.options.notification.display("Error, change NOT saved", 2000);
                    }
                }
            });
        });
    },

    initTabBar: function(){
        // tabs
        $(".tabs a").click(function(e){
            e.preventDefault();
            $(".tabs a").removeClass("active");
            $(this).addClass("active");
            $(".answer > div").hide();
            $(".answer > " + this.hash).fadeIn("fast");
        });
    },

    setTagBox: function() {
        var candidate = this;
        $("#mytags").tagit({
            availableTags: this.options.allTags,
            values: this.options.userTags
        });
        $("#mytags .tagit-input").bind("keydown keypress", function() {
            candidate.saveTags();
        });
    },

    saveTags: function(){
        var candidate = this;
        if (this.tagsTimer) {
            window.clearTimeout(this.tagsTimer);
        }
        this.tagsTimer = window.setTimeout(function(){
            // TODO: Check that the tags didn't change before pushing
            // Do the same notif for jeditable
            var tags = [];
            $(".tagit-choice", "#mytags").each(function(){
                tags.push($("input", this).attr("value"));
            });
            if (candidate.options.notification) {
                candidate.options.notification.display("Saving...");
            }
            $.ajax({
                type: 'POST',
                url: candidate.options.post,
                data: {
                    'resumeId': candidate.resumeId,
                    'id': 'tags',
                    'value': tags.join(" ")
                },
                success: function() {
                    if (candidate.options.notification) {
                        candidate.options.notification.display("Successfully saved", 2000);
                    }
                },
                error: function() {
                    if (candidate.options.notification) {
                        candidate.options.notification.display("Error, change NOT saved", 2000);
                    }
                }
            });
        }, 1000);
    }

};

window.Candidate = Candidate;

})(jQuery);
