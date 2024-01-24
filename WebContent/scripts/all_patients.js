const link = "https://hospital-maven-bcc4073ddde4.herokuapp.com/rest";
{/* <button id="new_record_button" class="add-btn">New Record</button> */}

let role = ''
let token = localStorage.getItem("token");
const all_patients = document.getElementById("all_patients");
const showUser = document.getElementById("showUsers");

//GET ROLE FROM API
const getRole = async () => {
	console.log("getRole() called");
	await jQuery.ajax({
		url: link + "/users/role" + `/${token}`,
		type: "GET",
		dataType: "json",
		headers: {
			"Authorization": "JWT " + localStorage.getItem("token")
		},
		success: function (resultData) {
			role = resultData.role;
			if (role == 'ADMIN') {
				all_patients.insertAdjacentHTML("beforeend", `<button id="new_record_button" class="add-btn">New Record</button>`);
				showUser.toggleAttribute("hidden");
				document.getElementById("new_record_button").onclick = () => {toggleDiv("add_record")}
			}
		},
		error: function (jqXHR, textStatus, errorThrown) { },
		timeout: 120000,
	})
}

//when document loads get role
document.addEventListener("DOMContentLoaded", () => {
	getRole();
});

showUser.onclick = () => {
	window.location.href = "https://hospital-maven-bcc4073ddde4.herokuapp.com/views/users.html";
}


const getDataFromREST = async (appendage) => {
	let data;

	await jQuery.ajax({
		url: link + appendage,
		type: "GET",
		dataType: "json",
		headers: {
			"Authorization": "JWT " + localStorage.getItem("token")
		},
		success: function (resultData) {
			data = resultData;
		},
		error: function (jqXHR, textStatus, errorThrown) { },
		timeout: 120000,
	})

	return data;
}

function toggleTables() {
	document.getElementById("patient_details").classList.toggle("hidden");
	document.getElementById("all_patients").classList.toggle("hidden");
}

function toggleDiv(divID, hiddenID) {
	document.getElementById(divID).classList.toggle("hidden");

	if(hiddenID) {
		//find child with name "id"
		let child = document.getElementById(divID).querySelector(`.hiddenID`);
		child.value = hiddenID;
	}
}

function cellEditButton(cell, row) {	
	if (cell) {
		return {
			onclick: () => {
				toggleDiv("edit_record", row.cells[0].data);
			},
			class: "edit-cell-button",
		};
	}
}

function cellDeleteButton(cell, row) {	
	if (cell) {
		return {
			onclick: () => {
				jQuery.ajax({
					url: link + `/tests/${row.cells[0].data}`,
					type: "DELETE",
					dataType: "json",
					contentType: "application/json",
					headers: {
						"Authorization": "JWT " + localStorage.getItem("token")
					},
					success: function (resultData) {
						alert("Record Deleted!");
					},
					error: function (jqXHR, textStatus, errorThrown) { },
					timeout: 120000,
				})
			},
			class: "delete-cell-button",
		};
	}
}

function renderDetailsTable(data, start_date, end_date) {
	// Remove the old table if it exists
	const details_header = document.getElementById("details_header");
	const chart_container = document.getElementById("chart_container");

	try {
		const parent = document.getElementById("patient_details");
		parent.removeChild(document.getElementById("patient_table"));

		chart_container.removeChild(document.getElementById('myChart'));
	} catch {console.log("No table or chart to remove");}
	
	// Replace it with a new empty div
	const patient_table_div = `<div id="patient_table" class="details-table"></div>`
	details_header.insertAdjacentHTML("afterend", patient_table_div);

	const chart_header = document.getElementById("chart_header");
	const chart_div = `<canvas id="myChart"></canvas>`
	chart_header.insertAdjacentHTML("afterend", chart_div);

	// NOTE: the above HAD to be done, because the table library was not allowing to empty the div
	// THIS was the best solution 

	const patient_details = document.getElementById("patient_table");

	getDataFromREST(`/tests/${data}/${start_date}/${end_date || new Date().toISOString().slice(0, 10)}`).then((data) => {
		let tests = [];
		let entry = [];

		let dates = [];
		let blood_levels = [];
		let carb_intakes = [];

		for (let i = 0; i < data.length; i++) {
			entry = [ data[i].test_id, data[i].patient_name, data[i].blood_glucose_level, data[i].carb_intake, data[i].medication_dose, data[i].created_at, 
			'Edit', 'Delete'];
			tests.push(entry);
			dates.push(data[i].created_at);
			blood_levels.push(data[i].blood_glucose_level);
			carb_intakes.push(data[i].carb_intake);
		}

		let columns = [
			{ name: "ID", width: '7%' }, 
			{ name: "Full Name" },
			{ name: "Blood Glucose Level", width: '15%' }, 
			{ name: "Carb Intake", width: '15%' }, 
			{ name: "Medication Dose", width: '15%' }, 
			{ name: "Date" }, 
		];

		if (role == 'ADMIN') {
			columns.push({ name: "", attributes: (cell, row) => cellEditButton(cell, row), width: '50px'});
			columns.push({ name: "", attributes: (cell, row) => cellDeleteButton(cell, row), width: '50px'});
		}

		new gridjs.Grid({
			columns: columns,
			data: (tests),
			pagination: {
				limit: 10,
				summary: false
			},
		}).render(patient_details);

		// CHART
		const chart = document.getElementById('myChart');
		new Chart(chart, {
			type: 'line',
			data: {
				labels: dates,
				datasets: [{
					label: 'blood glucose',
					data: blood_levels,
					borderWidth: 1
				},{
					label: 'carbon intake',
					data: carb_intakes,
					borderWidth: 1
				}]
			},
			options: {
				scales: {
					y: {
						beginAtZero: true
					}
				}
			}
		});
	});

	// AVERAGE VALUES
	getDataFromREST(`/tests/glucose/${data}/${start_date}/${end_date || new Date().toISOString().slice(0, 10)}`).then((result) => {
		document.getElementById("average_blood_glucose_level").innerHTML = result.toFixed(2);
	});

	getDataFromREST(`/tests/carbs/${data}/${start_date}/${end_date || new Date().toISOString().slice(0, 10)}`).then((result) => {
		document.getElementById("average_carb_intake").innerHTML = result.toFixed(2);
	});
}

function filterTests() {
	const start_date = document.getElementById("start_date").value;
	const end_date = document.getElementById("end_date").value;

	renderDetailsTable(patient_name, start_date, end_date);
}

function showDetails(data) {
	patient_name = data;

	toggleTables();
	renderDetailsTable(data, "1900-01-01");
}

// this function replaces the attributes of the td elements (cells)
function cellButton(cell) {
	// add these attributes to the td elements only
	if (cell) {
		return {
			onclick: () => showDetails(cell),
			class: "cell-button",
		};
	}
}

document.getElementById("logout").onclick = () => {
	localStorage.removeItem("token");
	window.location.href = "https://hospital-maven-bcc4073ddde4.herokuapp.com/";
}

let patient_name;

const filter_button = document.getElementById("filter_button");
filter_button.onclick = filterTests;

document.getElementById("back_button").onclick = (toggleTables);

// using the dridjs library, create and populate the ALL PATIENTS TABLE
getDataFromREST("/tests/patients").then((data) => {
	let names = [];
	let entry = [];
	for (let i = 0; i < data.length; i++) {
		entry = [ data[i] ];
		names.push(entry);
	}

	new gridjs.Grid({
		columns: [
			{ name: "Full Name", attributes: (cell) => cellButton(cell) },
		],
		data: (names),
		pagination: {
			limit: 10,
			summary: false,
		},
		search: true,
	}).render(document.getElementById("patients_table"));
});

// Add Functionality to ADD RECORD POPUP
document.getElementById("add_record_close").onclick = () => {toggleDiv("add_record")};

document.getElementById("add_record_button").onclick = () => {
	const patient_name = document.getElementById("add_patient_name").value;
	const blood_glucose_level = document.getElementById("add_blood_glucose_level").value;
	const carb_intake = document.getElementById("add_carb_intake").value;
	const medication_dose = document.getElementById("add_medication_dose").value;

	jQuery.ajax({
		url: link + "/tests",
		type: "POST",
		dataType: "json",
		contentType: "application/json",
		headers: {
			"Authorization": "JWT " + localStorage.getItem("token")
		},
		data: JSON.stringify({
			patient_name: patient_name,
			blood_glucose_level: blood_glucose_level,
			carb_intake: carb_intake,
			medication_dose: medication_dose
		}),
		success: function (resultData) {
			alert("Record Added!");
			toggleDiv("add_record");
		},
		error: function (jqXHR, textStatus, errorThrown) { },
		timeout: 120000,
	})
}

// Add Functionality to EDIT RECORD POPUP
document.getElementById("edit_record_close").onclick = () => {toggleDiv("edit_record")};

document.getElementById("edit_record_button").onclick = () => {
	const test_id = document.getElementById("edit_record_id").value;
	const patient_name = document.getElementById("edit_patient_name").value;
	const blood_glucose_level = document.getElementById("edit_blood_glucose_level").value;
	const carb_intake = document.getElementById("edit_carb_intake").value;
	const medication_dose = document.getElementById("edit_medication_dose").value;

	jQuery.ajax({
		url: link + `/tests/${test_id}`,
		type: "PUT",
		dataType: "json",
		contentType: "application/json",
		headers: {
			"Authorization": "JWT " + localStorage.getItem("token")
		},
		data: JSON.stringify({
			test_id: test_id,
			patient_name: patient_name,
			blood_glucose_level: blood_glucose_level,
			carb_intake: carb_intake,
			medication_dose: medication_dose
		}),
		success: function (resultData) {
			alert("Record Updated!");
			toggleDiv("edit_record");
			console.log(resultData);
		},
		error: function (jqXHR, textStatus, errorThrown) { },
		timeout: 120000,
	})
}

