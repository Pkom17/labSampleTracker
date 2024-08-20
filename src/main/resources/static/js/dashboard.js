$(document).ready(function() {
	$
		.get(
			"/api/tracker/sample_status_by_sample_type"+window.location.search,
			function(data) {
				Highcharts
					.chart(
						'sample_trend', {
						chart: {
							type: 'column'
						},
						title: {
							text: "Echantillons collectés, Transmis, Rejetés"
						},
						xAxis: {
							//categories: data.categories,
							categories: data.categories,
							crosshair: true
						},
						yAxis: {
							title: {
								text: "Nombre d'échantillons"
							}
						},
						legend: {
							enabled: true
						},
						plotOptions: {
							series: {
								allowPointSelect: true,
								dataLabels: {
									enabled: true,
								}
							}
						},
						tooltip: {
							headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
							pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
								'<td style="padding:0"><b>{point.y:.0f} </b></td></tr>',
							footerFormat: '</table>',
							shared: true,
							useHTML: true
						},

						series: [{
							name: "Echantillons collectés",
							data: data.collected,
							color: "#66F"
						},
						{
							name: "Echantillons Transmis",
							data: data.delivered,
							color: "#6E9"
						},
						{
							name: "Echantillons rejetés",
							data: data.nonConform,
							color: "#EE5599"
						}
						]
					});
			});


});