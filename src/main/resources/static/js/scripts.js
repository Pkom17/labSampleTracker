
$(document).ready(function() {
	var table = $('#datatablesSimple').DataTable({
		dom: 'Bfrtip',
		buttons: ['csv', 'excel', 'pdf']
	})
})


setPage = function(num) {
	const parser = new URL(window.location)
	if (num) {
		parser.searchParams.set('page', num)
	}
	window.location = parser.href
}


setSize = function(element) {
	const parser = new URL(window.location)
	var num = element.value;
	if (num) {
		parser.searchParams.set('size', num)
		parser.searchParams.set('page', 1)
	}
	window.location = parser.href
}


filterData = function() {
	let date1 = $('#input_start_date').val();
	let date2 = $('#input_end_date').val();
	let region = $('#select_region').val()
	let site = $('#select_site').val();
	let district = $('#select_district').val();
	const parser = new URL(window.location)
	parser.searchParams.set('page', 1)

	if (date1) {
		parser.searchParams.set('start', date1)
	} else {
		parser.searchParams.delete('start')
	}
	if (date2) {
		parser.searchParams.set('end', date2)
	} else {
		parser.searchParams.delete('end')
	}
	if (region) {
		if (district) {
			if (site) {
				parser.searchParams.delete('district');
				parser.searchParams.delete('region');
				parser.searchParams.set('site', site);
			}
			else {
				parser.searchParams.delete('site');
				parser.searchParams.delete('region');
				parser.searchParams.set('district', district);
			}
		} else {
			parser.searchParams.delete('district');
			parser.searchParams.delete('site');
			parser.searchParams.set('region', region);
		}
	}
	else {
		parser.searchParams.delete('region');
	}
	if (site) {
		parser.searchParams.delete('district');
		parser.searchParams.delete('region');
		parser.searchParams.set('site', site);
	}
	else {
		parser.searchParams.delete('site');
	}
	if (district) {
		if (site) {
			parser.searchParams.delete('district');
			parser.searchParams.delete('region');
			parser.searchParams.set('site', site);
		}
		else {
			parser.searchParams.delete('site');
			parser.searchParams.delete('region');
			parser.searchParams.set('district', district);
		}
	} else {
		parser.searchParams.delete('district');
	}

	window.location = parser.href
}


filterSampleList = function() {
	let date1 = $('#input_start_date').val();
	let date2 = $('#input_end_date').val();
	let region = $('#select_region').val()
	let site = $('#select_site').val();
	let district = $('#select_district').val();
	let status = $('#select_status').val();
	const parser = new URL(window.location)
	parser.searchParams.set('page', 1)

	if (status) {
		parser.searchParams.set('status', status)
	} else {
		parser.searchParams.delete('status')
	}
	if (date1) {
		parser.searchParams.set('start', date1)
	} else {
		parser.searchParams.delete('start')
	}
	if (date2) {
		parser.searchParams.set('end', date2)
	} else {
		parser.searchParams.delete('end')
	}
	if (region) {
		if (district) {
			if (site) {
				parser.searchParams.delete('district');
				parser.searchParams.delete('region');
				parser.searchParams.set('site', site);
			}
			else {
				parser.searchParams.delete('site');
				parser.searchParams.delete('region');
				parser.searchParams.set('district', district);
			}
		} else {
			parser.searchParams.delete('district');
			parser.searchParams.delete('site');
			parser.searchParams.set('region', region);
		}
	}
	else {
		parser.searchParams.delete('region');
	}
	if (site) {
		parser.searchParams.delete('district');
		parser.searchParams.delete('region');
		parser.searchParams.set('site', site);
	}
	else {
		parser.searchParams.delete('site');
	}
	if (district) {
		if (site) {
			parser.searchParams.delete('district');
			parser.searchParams.delete('region');
			parser.searchParams.set('site', site);
		}
		else {
			parser.searchParams.delete('site');
			parser.searchParams.delete('region');
			parser.searchParams.set('district', district);
		}
	} else {
		parser.searchParams.delete('district');
	}

	window.location = parser.href
}

resetFilter = function() {
	const parser = new URL(window.location);
	parser.searchParams.delete('page');
	parser.searchParams.delete('site');
	parser.searchParams.delete('district');
	parser.searchParams.delete('region');
	parser.searchParams.delete('start');
	parser.searchParams.delete('end');
	parser.searchParams.delete('status');
	window.location = parser.href
}

sortData = function(sortItem) {
	const parser = new URL(window.location)
	let existingSortQuery = parser.searchParams.get('sort')
	let sortDirectionString = 'desc'
	if (!parser.searchParams.has('sort')) {
		sortDirectionString = sortDirection()
	}
	else {
		existingSortItem = existingSortQuery.split(',')[0]
		if (existingSortItem === sortItem) {
			sortDirectionString = sortDirection(true)
		}
		else {
			sortDirectionString = sortDirection()
		}
	}
	parser.searchParams.set('sort', sortItem + ',' + sortDirectionString)
	window.location = parser.href
}

function sortDirection(next = false) {
	let directions = ['desc', 'asc']
	sortIndex = localStorage.getItem("sortIndex")
	if (!sortIndex) {
		sortIndex = 0
		localStorage.setItem("sortIndex", sortIndex)
	}
	else {
		if (next) {
			sortIndex = (parseInt(sortIndex) + 1) % 2
		}
		localStorage.setItem("sortIndex", sortIndex)
	}
	return directions[sortIndex]
}

exportToCSV = function() {
	const parser = new URL(window.location)
	var newLocation = parser.href.replace('sample', 'sample/csv')
	window.location = newLocation
}
