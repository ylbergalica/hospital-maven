const link = "http://localhost:8080/MyWebsite-0.0.1-SNAPSHOT/rest";

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

document.getElementById("logout").onclick = () => {
	localStorage.removeItem("token");
	window.location.href = "http://localhost:8080/MyWebsite-0.0.1-SNAPSHOT/index.html";
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
				toggleDiv("edit_user", row.cells[0].data);
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
					url: link + `/users/${row.cells[0].data}`,
					type: "DELETE",
					dataType: "json",
					contentType: "application/json",
					headers: {
						"Authorization": "JWT " + localStorage.getItem("token")
					},
					success: function (resultData) {
						alert("User Deleted!");
					},
					error: function (jqXHR, textStatus, errorThrown) { },
					timeout: 120000,
				})
			},
			class: "delete-cell-button",
		};
	}
}

// a table with all users
getDataFromREST("/users").then((data) => {
	let names = [];
	let entry = [];
	for (let i = 0; i < data.length; i++) {
		entry = [ data[i].user_id, data[i].username, data[i].power_level, 'Edit', 'Delete' ];
		names.push(entry);
	}

	new gridjs.Grid({
		columns: [
			{ name: "ID", width: "7%" }, { name: "Full Name"}, { name: "Power Level" }, {name: "", attributes: (cell, row) => cellEditButton(cell, row), width: '50px'}, {name: "", attributes: (cell, row) => cellDeleteButton(cell, row), width: '50px'}
		],
		data: (names),
		pagination: {
			limit: 10,
			summary: false,
		},
		search: true,
	}).render(document.getElementById("users_table"));
});

// Add Functionality to CLOSE POPUPs
document.getElementById("add_user_close").onclick = () => {toggleDiv("add_user")};
document.getElementById("edit_user_close").onclick = () => {toggleDiv("edit_user")};

// ADD USER FUNCTIONALITIES
document.getElementById("new_user_button").onclick = () => {toggleDiv("add_user")}
document.getElementById("add_user_button").onclick = () => {
	const username = document.getElementById("add_username").value;
	const password = document.getElementById("add_password").value;
	const power_level = document.querySelector('input[name="add_power_level"]:checked').value;

	jQuery.ajax({
		url: link + "/users",
		type: "POST",
		dataType: "json",
		contentType: "application/json",
		headers: {
			"Authorization": "JWT " + localStorage.getItem("token")
		},
		data: JSON.stringify({
			username: username,
			password: password,
			power_level: power_level
		}),
		success: function (resultData) {
			alert("User Added!");
			toggleDiv("add_user");
		},
		error: function (jqXHR, textStatus, errorThrown) { },
		timeout: 120000,
	})
}

// EDIT USER FUNCTIONALITIES
document.getElementById("edit_user_button").onclick = () => {
	const user_id = document.getElementById("edit_user_id").value;
	const username = document.getElementById("edit_username").value;
	const password = document.getElementById("edit_password").value;
	const power_level = document.querySelector('input[name="edit_power_level"]:checked').value;

	jQuery.ajax({
		url: link + `/users/${user_id}`,
		type: "PUT",
		dataType: "json",
		contentType: "application/json",
		headers: {
			"Authorization": "JWT " + localStorage.getItem("token")
		},
		data: JSON.stringify({
			username: username,
			password: password,
			power_level: power_level
		}),
		success: function (resultData) {
			alert("User Updated!");
			toggleDiv("edit_user");
		},
		error: function (jqXHR, textStatus, errorThrown) { },
		timeout: 120000,
	})
}