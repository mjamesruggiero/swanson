$(document).ready(function(){
    var swanson = {
        weeklyData: function() {
            var json = $.ajax({ url: "/by-week",
                                dataType: "json",
                                async: false }).responseText;

            var parsed = JSON.parse(json);

            var labels = _.map(parsed, function(w){ return w.week; });
            var values = _.map(parsed, function(w){ return w.total; });

            var dataset = { data: values };
            return { labels: labels, datasets: [dataset] };
        },

        monthlyData: function() {
            var json = $.ajax({ url: "/months",
                                dataType: "json",
                                async: false }).responseText;

            var parsed = JSON.parse(json);
            var labels = _.map(parsed, function(m) { return m.month + "-" + m.year; });
            var values = _.map(parsed, function(m) { return m.amount; });

            var dataset = { data: values };
            return { labels: labels, datasets: [dataset] };
        },

        init: function() {
            // weekly chart
            var ctx = document.getElementById("weekly-chart").getContext("2d");
            var weeklyChart = new Chart(ctx).Bar(swanson.weeklyData(), {});

            // monthly chart
            ctx = document.getElementById("monthly-chart").getContext("2d");
            var monthlyChart = new Chart(ctx).Bar(swanson.monthlyData(), {});
        }
    };
    swanson.init();
});
