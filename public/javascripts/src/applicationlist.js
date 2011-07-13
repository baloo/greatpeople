// Controller

/*jshint sub: true, strict: true */
/*global Backbone: false, jQuery: false, _: false, window: false */

(function($) {

"use strict";

// Model

var ApplicantModel = Backbone.Model.extend({

    initialize: function() {
        this.set({
            "stars": this.get("rating") ? this.get("rating") * 100 / 4 : 0
        });
    }

});

var ApplicantList = Backbone.Collection.extend({
    model: ApplicantModel,
    box: 'new',
    url: '/api/applicants',
    query: null,
    page: 0,
    pageCount: 1,

    parse: function(response) {
        this.page = response.pageNumber;
        this.pageCount = response.pageCount;
        return response.applications;
    }

});

var Applicants = new ApplicantList();

// Views

var ApplicantView = Backbone.View.extend({
    tagName: 'dl',
    className: 'candidate-list',
    render: function() {
        $("#applicantTmpl").tmpl(this.model.toJSON()).appendTo(this.el);
        return this;
    }
});

var AppView = Backbone.View.extend({

    el: $("#applist")[0],

    initialize: function() {
        _.bindAll(this, "render");
        Applicants.bind("reset", this.render);
    },

    refreshLinks: function(query) {
        var q = query ? "/" + query.split(",")[0] : "";
        $(["new", "inprogress", "archived"]).each(function(){
            $(".navlink." + this + " a").attr("href", "#" + this + q);
        });
    },

    spinner: function() {
        $("#spinner").show();
    },

    render: function() {
        $("#spinner").hide();
        var $view = $(this.el).html('');
            if (Applicants.length === 0) {
            // Render the "no applications" message
            $view.append($("#noApplicantTmpl").tmpl());
            return;
        }

        var box = Applicants.box;
        Applicants
            .map(function(applicant) { return new ApplicantView({model: applicant}); })
            .forEach(function(entry) {
                $view.append(entry.render().el);
            });
        $(".tags > ul > li > a").each(function(){
            $(this).attr("href", "#" + box + "/tag:" + $(this).attr("data"));
        });

        // Internal page number starts at 0, but we want to start at 1
        var pagination = $("#pagination");
        pagination.html("");
        if (Applicants.pageCount > 1) {
            _(Applicants.pageCount).times(function (i) {
                pagination.append(
                    Applicants.page == i ? i + 1         // Current page: just the page number
                        : ($("#pageLink").tmpl({
                            page: i + 1,
                            link: "#" + Applicants.box +
                                  "/" + (Applicants.query ? Applicants.query : "") +
                                  "," + (i + 1)
                       })
                    )
                );
                pagination.append("&nbsp;");
            });
        }
        $("#pagination").toggle(Applicants.pageCount > 1);
    },

    login: function() {
        window.location = "/login?callback=" + encodeURIComponent(window.location);
    }

});

/*
var PaginationView = Backbone.View.extend({

    el: $("#pagination")[0],

    render: function() {
        // Internal page number starts at 0, but we want to start at 1
        var pagination = $(this.el).html("");
        if (this.model.pageCount > 1) {
            _(this.model.pageCount).times(function (i) {
                pagination.append(
                    this.model.page == i ? i + 1         // Current page: just the page number
                        : ($("#pageLink").tmpl({
                            page: i + 1,
                            link: "#" + this.model.box +
                                  "/" + (this.model.query ? this.model.query : "") +
                                  "," + (i + 1)
                       })
                    )
                );
                pagination.append("&nbsp;");
            });
        }
        return this;
    }

});
*/
var Workspace = Backbone.Router.extend({

    routes: {
        "": "index",
        "new": "newapp",    // #new
        "new/:query": "newapp",  // #new/kiwis
        "inprogress": "inprogress",
        "inprogress/:query": "inprogress",
        "archived": "archived",
        "archived/:query": "archived"
    },

    initialize: function() {
        this.view = new AppView({});
    },

    index: function() {
        window.location.hash = "#new";
    },

    newapp: function(query) {
        this._showBox("new", query);
    },

    inprogress: function(query) {
        this._showBox("inprogress", query);
    },

    archived: function(query) {
        this._showBox("archived", query);
    },

    _showBox: function(active, query, page) {
        this.box = active;
        // this.view.loadBox(active, query, page);
        $("#search").val(query ? query.split(",")[0] : "");
        // this.refreshLinks(query);
        Applicants.box = this.box;
        Applicants.url = '/api/applicants/' + this.box;
        if (query) {
            Applicants.query = query;
            Applicants.page = 1;
            if (query.indexOf(",") > -1) {
                Applicants.query = query.split(",")[0];
                Applicants.page = query.split(",")[1];
            }
            Applicants.url += (
                '?q=' + encodeURIComponent(Applicants.query) +
                '&p=' + Applicants.page
            );
        } else {
            Applicants.query = "";
            Applicants.page = 1;
        }
        Applicants.fetch();
        $(".navlink a").removeClass("active");
        $("." + active + " a").addClass("active");
    }
});

window["Workspace"] = Workspace;

})(jQuery);
