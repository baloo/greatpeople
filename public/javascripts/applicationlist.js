// Controller

/*global Backbone */

(function($){

var Workspace = Backbone.Controller.extend({

    routes: {
        "": "index",
        "new": "newapp",    // #new
        "new/:query": "newapp",  // #new/kiwis
        "inprogress": "inprogress",
        "inprogress/:query": "inprogress",
        "archived": "archived",
        "archived/:query": "archived",
        "search/:query/p:page": "search"   // #search/kiwis/p7
    },

    initialize: function() {
        this.apps = new Applicants;
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

    search: function(query, page) {
        // todo
    },

    _showBox: function(active, query) {
        this.box = active;
        this.view.loadBox(active, query);
        $(".navlink a").removeClass("active");
        $("." + active + " a").addClass("active");
    }

});

// Views

var AppView = Backbone.View.extend({

    el: $("#list"),

    loadBox: function(box, query) {
        this.collection.url = '/api/applicants/' + box;
        if (query) {
            this.collection.url += ('?q=' + encodeURIComponent(query));
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

    spinner: function() {
        $("#spinner").show();
    },

    render: function() {
        $("#spinner").hide();
        var $view = $(this.el).html('');

        if(this.collection.length === 0) {
            // Render the "no applications" message
            $view.append( $("#noApplicantTmpl").tmpl() );
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
    },

    login: function() {
        window.location = "/login?callback=" + encodeURIComponent(window.location);
    }

});

// Model

var ApplicantModel = Backbone.Model.extend({
});

var Applicants = Backbone.Collection.extend({
    model: ApplicantModel,
    url: '/api/applicants'
});

window.Workspace = Workspace;

})(jQuery);
