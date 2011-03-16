
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
                width: self.id == "name" ? "auto" : 100
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
            var tags = [];
            $(".tagit-choice", "#mytags").each(function(){
                tags.push($("input", this).attr("value"));
            });
            $.post(candidate.options.post, {
                'resumeId': candidate.resumeId,
                'id': 'tags',
                'value': tags.join(" ")
            });
        }, 1000);
    }

};

window.Candidate = Candidate;

})(jQuery);
