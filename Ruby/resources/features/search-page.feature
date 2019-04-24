Feature: Search Page

Background:

	Given I visit the website

#1
	Scenario: the default page should be Search Page

		When I visit the website
		Then I should see the "Search" page
		And There should be a "login" button
		And There should be a "signup" button

#2
	Scenario: if you login with wrong password it displays invalid password

		When I press "login" button
		And enter "testuser" into "username"
		And enter "wrongpassword" into "password"
		And press "submit" button
		Then I should see "Invalid Password!" text

#3
	Scenario: if you login with not a real user name it displays invalid username

		When I press "login" button
		And enter "fakeuser" into "username"
		And enter "password" into "password"
		And press "submit" button
		Then I should see "Invalid Username!" text

#4

Scenario: initiating the search redirects to Results Page if I am logged in; make a search
	add a result to grocery list, check that it is there, go back to search & check that
	the previous Search was added to the previous searches and when clicked on
	will fill in the appropriate fields with appropriate values

	When I press "login" button
    And enter "testuser" into "username"
    And enter "password" into "password"
    And press "submit" button
    And I should see the "Search" page
	And I should see prevSearch dropdown
	And I search for "garden"
	And expect 16 results
	And enter radius of 10
	And press "submit" button
	And I should see the "Result" page
	And I should see a "page1" pagination button
	And I should see a "page2" pagination button
	And I should see a "page3" pagination button
	And press a recipe
	And click on dropdown
	And click the "Grocery" selector
	And press "addtolist" button
	And press "backtoresults" button
	And click on dropdown
	And click the "Grocery" selector
	And press "manage_list" button
	And press "back_search" button
    And click prevSearch dropdown
	And click the "prev_search0" selector
	Then I should see "garden" in "search"
	And  I should see "10" in "radius"
	And I should see "16" in "number"

