// Controller

/*global Backbone, Underscore */

(function($) {

// Model

var ApplicantModel = Backbone.Model.extend({
});

var Applicants = Backbone.Collection.extend({
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

// Views

var AppView = Backbone.View.extend({

    el: $("#list"),

    loadBox: function(box, query) {
        // $("#search").val(query,split(",")[0]);
        this.refreshLinks(query);
        this.collection.box = box;
        this.collection.url = '/api/applicants/' + box;
        if (query) {
            this.collection.query = query;
            this.collection.page = 1;
            if (query.indexOf(",") > -1) {
                this.collection.query = query.split(",")[0];
                this.collection.page = query.split(",")[1];
            }
            this.collection.url += (
                '?q=' + encodeURIComponent(this.collection.query) +
                '&p=' + this.collection.page
            );
        } else {
            this.collection.query = "";
            this.collection.page = 1;
        }
        var view = this;
        this.spinner();
        this.collection.fetch({
            success: function() {
                view.render();
            },
            error: function(model, response) {
                if (response.status == 403) {
                    view.login();
                }
            }
        });
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
        if (this.collection.length === 0) {
            // Render the "no applications" message
            $view.append($("#noApplicantTmpl").tmpl());
            return;
        }

        this.collection.forEach(function(applicant) {
            var attr = applicant.toJSON();
            $view.append($("#applicantTmpl").tmpl(
                _(attr).extend({
                    stars: attr.rating * 100 / 4
                })
            ));
        });

        // Internal page number starts at 0, but we want to start at 1
        var pagination = $("#pagination");
        pagination.html("");
        if (this.collection.pageCount > 1) {
            for (var i = 0; i < this.collection.pageCount; i++) {
                if (this.collection.page == i) {
                    // Current page: just the page number
                    pagination.append(i + 1);
                } else {
                    // Other: include a link
                    pagination.append(
                        $("<a></a>")
                            .text(i + 1)
                            .attr("href", "#" +
                                  this.collection.box +
                                  "/" +
                                  (this.collection.query ? this.collection.query : "") +
                                  "," + (i + 1))
                    );
                }
                pagination.append("&nbsp;");
            }
        }
        $("#pagination").toggle(this.collection.pageCount > 1);
    },

    login: function() {
        window.location = "/login?callback=" + encodeURIComponent(window.location);
    }

});

var Workspace = Backbone.Controller.extend({

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
        this.apps = new Applicants();
        this.view = new AppView({
            collection: this.apps
        });
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
        this.view.loadBox(active, query, page);
        $(".navlink a").removeClass("active");
        $("." + active + " a").addClass("active");
    }

});

window.Workspace = Workspace;

})(jQuery);
