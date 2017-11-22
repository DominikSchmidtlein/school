// function only gets called after page has loaded
window.onload = function() {
  // retrieve a reference to the figure element that displays the large image
  var figure = document.querySelector("figure")
  var figcaption = document.querySelector("figcaption")
  // add a listener for when the mouse moves onto the figure
  figure.addEventListener("mouseover", function(event) {
    showCaption(figcaption, event)
  })
  // add a listener for when the mouse moves off of the figure
  figure.addEventListener("mouseout", function(event) {
    hideCaption(figcaption, event)
  })
  // retrieve a reference to the large image
  var preview = document.querySelector(".preview")
  // retrieve a nodelist with a reference to every thumnail image
  var thumbnails = document.querySelectorAll(".thumbnail")
  // on each thumbnail, add an onclick listener that calls setPreviewImage
  thumbnails.forEach(function(thumbnail) {
    thumbnail.addEventListener("click", function(event) {
      setPreviewImage(preview, figcaption, event)
    })
  })
}

// set the src, title and alt attributes and caption of the image in the figure
function setPreviewImage(preview, figcaption, event) {
  preview.src = event.target.src.replace("small", "medium")
  preview.alt = event.target.alt
  preview.title = event.target.title
  figcaption.innerText = event.target.title
}
// make the caption visible and update the text it contains
function showCaption(figcaption, event) {
  figcaption.hidden = false
}
// make the caption invisible
function hideCaption(figcaption, event) {
  figcaption.hidden = true
}
