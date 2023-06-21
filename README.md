# Fetch Coding Exercise
This is my submission for **Fetch Rewards Coding Exercise - Software Engineering - Mobile**

This is a coding exercise as part of the interview process for Fetch, the shopping rewards app. This application, written in Kotlin using Android Studio (API 33) and tested on an emulated Pixel 6, displays a list of item names located in the json file provided by the recruiter. These items are displayed first by their listId, then name. The user can choose to filter which lists they would like to view, and they can chose how to sort the items. Lexicographically sorting will display "Item 100" before "Item 2," as is the nature of strings. Numerically sorting will display "Item 2" before "Item 100", but this does not sort on the id field in case the id does not match the name.

## Requirements
The requirements provided are as follows:
- Display all the items grouped by "listId"
- Sort the results first by "listId" then by "name" when displaying.
- Filter out any items where "name" is blank or null.

It was also requested to "make the project buildable on the latest (non-pre release) tools and supporting the current release mobile OS."

## User Guide
The application displays a vertical-scrolling list of item names. Each item's **id** and **listId** are *not* displayed next to the **name** to avoid clutter and over-redundancy.
The list is grouped by **listId** which is displayed as a header above each sublist.
When none of the filter boxes are checked, all **listId** lists will be displayed. This is the default.
Checking boxes filters the list so that only the checked lists are displayed.
Choosing to sort by *lexicographic* will display "Item 100" before "Item 2."
Choosing to sort by *numerical* will display "Item 2" before "Item 100."

## Potential Future Improvements
As this dataset provided contains erronous data, the app could provide the means for an administrator to login and correct the data. This would require data management to save the state of the data, and would also require user authentication through a service such as firebase. Note the application is already designed to hold information on what data is erronous and why.

UI improvements that could be made include collapsible lists for each **listId** sublist. This may be desired with/over filtering.