# ImageSearchAssignment

The home screen of the app has a search field and an options menu. The functionality of the app are as follows:

search
where the user types a search tearm in the search field and progress done the application communicates with a image search api(you can use google images api, flickr api or any other image API your prefer)
) and displays the result images in a grid layout below the search filed.
The Images must be shown as square views in the grid without any skewing

OptionMenu
The number of columns in the grid can be changed to 2, 3 or 4 columns from the options menu(by default the grid is 2 column). Changing the number of columns should NOT re-invoke the API, it must be handled at UI level.

Paging:
 The grid layout has infinite scroll support
example:
if the user scrolls to the bottom of the page, then the app will be re-contact the image search API to get more  results and add them to the bottom of the grid.

Offline Support
 Whatever terms the user has searched as well as the associated search results need to be persisted(choice of persistence is left to developer).
So if the user is offline and performs a search for a term for which the results were saved earlier, the app will display the saved results maintaining the order as it was fetched from the API.

 When user clicks on an image in the grid, perform a shared element transition of the square image to full screen image on a new activity. Note that the full screen image is  not a square, it maintains the original aspect of the image,
 
 so the shared element transition must take this into account.Pressing back  from  the full screen activity should do a reverse transition from the full screen image to the square image in the grid
