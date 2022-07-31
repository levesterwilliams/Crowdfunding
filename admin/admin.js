// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));


// set up EJS
app.set('view engine', 'ejs');

const {Organization} = require('./DbConfig.js');
const {Fund} = require('./DbConfig.js');
const {Contributor} = require('./DbConfig.js');
const {Donation} = require('./DbConfig.js');




/*****************************************************/
/* ORGANIZATIONS */


/*
List all organizations
*/
app.use('/allOrgs', (req, res) => {
	Organization.find({}, (err, result) => {
		if (err) {
		    res.render("error", { "error" : err });
		}
		else {
		    //console.log(result);
		    res.render("allOrgs", { "result" : result} );
		}
	    }).sort({ 'name': 'asc' });
    });


/*
Handle the form submission to create a new organization
*/
app.use('/createOrg', (req, res) => {

	var org = new Organization({
		login: req.body.login,
		password: req.body.password,
		name: req.body.name,
		description: req.body.description,
		funds: []
	    });

	org.save( (err) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(org);
		    res.render("viewOrg", { 'org': org , 'status': 'Successfully created new Organization'});
		}
	    });

    });

/*
View info about the org with ID specified as req.query.id
*/
app.use('/viewOrg', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Organization.findOne( query, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(result);
		    res.render("viewOrg", {"org" : result, 'status' : 'Viewing Organization'});
		}
	    });

    });


/*
Display the form used to edit the org with ID specified as req.query.id
*/
app.use('/editOrg', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Organization.findOne( query, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(result);
		    res.render("editOrg", {"org" : result});
		}
	    });

    });

/*
Handle the form submission to update an org
*/
app.use('/updateOrg', (req, res) => {

	var filter = {"_id" : req.body.id };

	var update = { "login" : req.body.login, "password" : req.body.password, "name" : req.body.name, "description" : req.body.description };
	
	var action = { "$set" : update };

	Organization.findOneAndUpdate( filter, action, { new : true }, (err, result) => {
		if (err) {
		    res.render("error", { 'error' : err });
		}
		else {
		    //console.log(result);
		    res.render("viewOrg", {"org" : result , 'status' : 'Successfully updated Organization'});
		}
	    });
	
    });

/*
Handle the form submission to update an org's password
*/
app.use('/updateOrgPassword', (req, res) => {

	var filter = {"_id" : req.body.id };

	var update = { "password" : req.body.password };
	
	var action = { "$set" : update };

	Organization.findOneAndUpdate( filter, action, { new : true }, (err, result) => {
		if (err) {
		    res.render("error", { 'error' : err });
		}
		else {
		    //console.log(result);
		    res.render("viewOrg", {"org" : result , 'status' : 'Successfully updated Organization Password'});
		}
	    });
	
    });


/*
Delete the org with ID specified as req.query.id
*/
app.use('/deleteOrg', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Organization.findOneAndDelete( query, (err, orig) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    // console.log(orig);
		    res.render("deletedOrg", { "org" : orig });
		}
	    });

    });




/*****************************************************/
/* FUNDS */

/*
Display the form to create a new fund in the org with ID specified as req.query.id
*/
app.use('/newFund', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Organization.findOne( query, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    // console.log(result);
		    res.render("newFund", { "org" : result });
		}
	    });

    });

/*
Handle the form submission to create a new fund.
*/
app.use('/createFund', (req, res) => {

	var fund = new Fund({
		name: req.body.name,
		description: req.body.description,
		target: req.body.target,
		org: req.body.orgId,
		donations: []
	    });

	fund.save( (err) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(fund);

		    /*
		      In addition to updating the Fund collection,
		      we also need to update the Fund in the Organization document/object.
		    */
		    var filter = {"_id" : req.body.orgId };

		    var action = { "$push" : { "funds" : fund } };

		    Organization.findOneAndUpdate( filter, action, (err, result) => {
			    if (err) {
				res.render("error", { 'error' : err });
			    }
			    else {
				//console.log(result);
				//res.render("viewFund", {'fund' : fund , 'status' : "Successfully created new Fund"});				
				res.redirect('/viewFund?id=' + fund.id);
			    }
			});
		
		}
	    });

    });


/*
View information about the fund with ID specified as req.query.id
*/
app.use('/viewFund', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Fund.findOne( query, (err, result) => {
		if (err) {
		    res.render('error', {'error' : err});
		}
		else {
		    //console.log(result);

		    /*
		      The Fund holds the IDs of all the Contributors.
		      This code gets the Contributors' names and puts them
		      into the object that is returned.
		    */

		    var contributorIds = [];
		    result.donations.forEach( (donation) => {
			    contributorIds.push(donation.contributor);
			});


		    var filter = {"_id" : { "$in" : contributorIds } };
		    Contributor.find(filter, (err, all) => {
			    
			    var map = new Map();
		    
			    all.forEach((contributor) => {
				    map.set(String(contributor._id), contributor.name);
				});
			    
			    result.donations.forEach( (donation) => {
				    donation.contributorName = map.get(donation.contributor);
				});

			    /*
			      This is so we can display contributor names in the page
			      to view a fund, so that we can make contributions from there.
			    */
			    Contributor.find( (err, allContributors) => {
				    if (err) {
					res.render("error", {"error": err});
				    }
				    else {
					res.render("viewFund", {"fund" : result, "status" : "Viewing Fund", "allContributors" : allContributors});
				    }

				}).sort({ 'name': 'asc' });


			});


		}
	    });

    });

/*
Displays the form to edit the fund with the ID specified as req.query.id
*/
app.use('/editFund', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Fund.findOne( query, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(result);
		    res.render("editFund", {"fund" : result});
		}
	    });

    });

/*
Make a new donation to the fund with ID specified as req.query.fund_id.
Note that the contributor's ID is specified in the body (of the form submission)
as req.body.contributor_id and not as a query parameter.
*/
app.use('/makeDonation', (req, res) => {

	var donation = new Donation({
		contributor: req.body.contributor_id,
		fund: req.query.fund_id,
		date: new Date(),
		amount: req.body.amount
	    });
	
	donation.save( (err) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(donation);

		    /*
		      In addition to adding to the Donation collection,
		      we also need to add this Donation to the Contributor object/document.
		    */

		    var filter = { "_id" : req.body.contributor_id };
		    var action = { "$push" : { "donations" : donation } };

		    Contributor.findOneAndUpdate(filter, action, (err) => {

			    if (err) {
				res.render('error', {'error' : err});
			    }
			    else {
				
				/*
				  Next, we need to add this Donation to the Fund object.
				 */

				var filter = { "_id" : req.query.fund_id };
				var action = { "$push" : { "donations" : donation } };

				Fund.findOneAndUpdate(filter, action, { "new" : true }, (err, fund) => {

					if (err) {
					    res.render('error', {'error' : err});
					}
					else {

					    /*
					      Once we've done that, we need to update the Fund object
					      that is in the Org object. I am doing that by removing
					      the Fund object, updating it, and then putting it back. 
					     */

					    var query = { "_id" : fund.org };

					    var whichFund = { "funds" : { "_id" : { "$in" : [fund._id] }  } }; 

					    var action = { "$pull" : whichFund };

					    Organization.findOneAndUpdate(query, action, (err, org) => {


						    if (err) {
							res.render('error', {'error' : err});
						    }
						    else {    
							
							/*
							  Now that we removed the Fund object, put back the updated one.
							*/

							var query = { "_id" : fund.org };
					    
							var action = { "$push" : { "funds" : fund } };
							
							Organization.findOneAndUpdate(query, action, { "new" : true }, (err, org) => {
								
								if (err) {
								    res.render('error', {'error' : err});
								}
								else {
								    // Using a redirect here since we go back to the "viewFund" page now
								    res.redirect('/viewFund?id=' + fund.id);
								}


							    });


						    }

						});

					}
				    });
			    }


			});


		}
	    });



    });

/*
Handles the form submission to update a fund
*/
app.use('/updateFund', (req, res) => {

	var filter = {"_id" : req.body.id };

	var update = { "name" : req.body.name, "description" : req.body.description, "target" : req.body.target };
	
	var action = { "$set" : update };

	Fund.findOneAndUpdate( filter, action, { new : true }, (err, result) => {
		if (err) {
		    res.render("error", { 'error' : err });
		}
		else {
		    //console.log(result);

		    /*
		      As above, because the Fund's donations hold the Contributor IDs and we 
		      want to display their names, this code gets the names from the Contributor 
		      collection and updates the Fund objects.
		    */

		    var contributorIds = [];
		    result.donations.forEach( (donation) => {
			    contributorIds.push(donation.contributor);
			});


		    var filter = {"_id" : { "$in" : contributorIds } };
		    Contributor.find(filter, (err, all) => {
			    
			    var map = new Map();
		    
			    all.forEach((contributor) => {
				    map.set(String(contributor._id), contributor.name);
				});
			    
			    result.donations.forEach( (donation) => {
				    donation.contributorName = map.get(donation.contributor);
				});

			    
			    Contributor.find( (err, allContributors) => {
				    if (err) {
					res.render("error", {"error": err});
				    }
				    else {
					res.render("viewFund", {"fund" : result, "status" : "Viewing Fund", "allContributors" : allContributors});
				    }

				}).sort({ 'name': 'asc' });

			});

		}
	    });

    });


/*
Deletes the fund with ID specified as req.query.id
*/
app.use('/deleteFund', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Fund.findOneAndDelete( query, (err, orig) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(orig);

		    /*
		      Also need to delete it from the Org to which it belongs
		    */

		    var filter = {"_id" : orig.org };
		    var action = { "$pull" : { "funds" : { "_id" : req.query.id } } };

		    Organization.findOneAndUpdate(filter, action, (err) => {
			    if (err) {
				res.render("error", {'error' : err});
			    }
			    else {
				res.render("deletedFund", {"fund": orig});
			    }
			});
		}
	    });

    });




/*****************************************************/
/* CONTRIBUTORS */

/*
List all contributors
*/
app.use('/allContributors', (req, res) => {
	Contributor.find({}, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(result);
		    res.render("allContributors", {"result" : result});
		}
	    }).sort({ 'name': 'asc' });
    });

/*
Handle the form submission to create a new contributor
*/
app.use('/createContributor', (req, res) => {

	var contributor = new Contributor({
		login: req.body.login,
		password: req.body.password,
		name: req.body.name,
		email: req.body.email,
		creditCardNumber: req.body.card_number,
		creditCardCVV: req.body.card_cvv,
		creditCardExpiryMonth: req.body.card_month,
		creditCardExpiryYear: req.body.card_year,
		creditCardPostCode: req.body.card_postcode,
		donations: []
	    });

	contributor.save( (err) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(contributor);
		    res.render("viewContributor", {"contributor": contributor, "status" : "Successfully created new Contributor"});
		}
	    });

    });


/*
View the contributor with ID specified as req.query.id
*/
app.use('/viewContributor', (req, res) => {

	var query = {"_id" : req.query.id };

	Contributor.findOne( query, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(result);

		    /*
		      Because the Contributor's donations store the Funds' IDs and we want
		      to list their names, this code gets the names of the Funds and then
		      updates the donation objects.
		    */

		    var fundIds = [];
		    result.donations.forEach( (donation) => {
			    fundIds.push(donation.fund);
			});


		    var filter = {"_id" : { "$in" : fundIds } };
		    Fund.find(filter, (err, all) => {
			    
			    var map = new Map();
		    
			    all.forEach((fund) => {
				    map.set(String(fund._id), fund.name);
				});
			    
			    result.donations.forEach( (donation) => {
				    donation.fundName = map.get(donation.fund);
				});

			    
			    res.render("viewContributor", {"contributor": result, "status" : "Viewing Contributor"});
			    
			});


		}
	    });


    });


/*
Display the form to edit the Contributor with ID specified as req.query.id
*/
app.use('/editContributor', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Contributor.findOne( query, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(result);
		    res.render("editContributor", {"contributor" : result});
		}
	    });

    });

/*
Handle the form submission to update a contributor
*/
app.use('/updateContributor', (req, res) => {

	var filter = {"_id" : req.body.id };

	var update = { "login" : req.body.login, "password" : req.body.password, "name" : req.body.name, "email" : req.body.email, "creditCardNumber" : req.body.card_number, "creditCardCVV" : req.body.card_cvv, "creditCardExpiryMonth" : req.body.card_month, "creditCardExpiryYear": req.body.card_year, "creditCardPostCode" : req.body.card_postcode };
	
	var action = { "$set" : update };

	Contributor.findOneAndUpdate( filter, action, { new : true }, (err, result) => {
		if (err) {
		    res.render("error", { 'error' : err });
		}
		else {
		    //console.log(result);

		    /*
		      As above, because we want to display the names of the funds to which the
		      contributor has donated, this code gets their names and then updates this
		      contributor's donation objects.
		    */

		    var fundIds = [];
		    result.donations.forEach( (donation) => {
			    fundIds.push(donation.fund);
			});


		    var filter = {"_id" : { "$in" : fundIds } };
		    Fund.find(filter, (err, all) => {
			    
			    var map = new Map();
		    
			    all.forEach((fund) => {
				    map.set(String(fund._id), fund.name);
				});
			    
			    result.donations.forEach( (donation) => {
				    donation.fundName = map.get(donation.fund);
				});

			    
			    res.render("viewContributor", {"contributor": result, "status" : "Viewing Contributor"});
			    
			});


		}
	    });
	
    });


/*
Delete the contributor with ID specified as req.query.id
*/
app.use('/deleteContributor', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Contributor.findOneAndDelete( query, (err, orig) => {
		if (err) {
		    res.render("error", {'error' : err});
		}
		else {
		    //console.log(orig);
		    res.render("deletedContributor", {"contributor": orig});
		}
	    });

    });


			 
/********************************************************/

app.use('/forms', express.static('forms'));

app.use('/', (req, res) => {
	res.write("<a href=\"/allOrgs\">Administer Organizations</a>");
	res.write("<p>");
	res.write("<a href=\"/allContributors\">Administer Contributors</a>");
	res.send();
    });

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
