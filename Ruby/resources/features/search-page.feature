Feature: Search Page

Background:

	Given I visit the website



Scenario: initiating the search redirects to Results Page if I am logged in; make a search
	add a result to grocery list, check that it is there, go back to search & check that
	the previous Search was added to the previous searches and when clicked on
	will fill in the appropriate fields with appropriate values

	When I press "login" button
    And enter "testuser" into "username"
    And enter "password" into "password"
    And press "submit" button
    And I should see the "Search" page
	And I search for "garden"
	And expect 16 results
	And enter radius of 10
	And press "submit" button
	And I should see the "Result" page
	And I should see a "Next" button with value
	And I should see a "1" pagination button
	And I should see a "2" pagination button
	And I should see a "Prev" button with value
	And I should see a "3" pagination button
	And press a recipe
	And click on dropdown
	And click the "Grocery" selector
	And press "addtolist" button
	And press "backtoresults" button
	And click on dropdown
	And click the "Grocery" selector
	And press "manage_list" button
	And I select "1. ingredient" unchecked
	And press "back_search" button


