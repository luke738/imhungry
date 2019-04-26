Feature: List Management Page

Background:

	Given I visit the website

	#1
	Scenario: page requires a login

		When I visit the list page
		Then I should see the "Login Page" page
		And enter "testuser" into "username"
		And enter "password" into "password"
		And press "submit" button
		And I search for "chicken"
		And expect 5 results
		And press "submit" button
		And select the list "Favorites"
		And press "manage_list" button
		And click down on item1
		Then item1 has moved




