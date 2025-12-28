export function showSkeleton(selector) {
  document.querySelector(selector)?.classList.add("skeleton");
}

export function hideSkeleton(selector) {
  document.querySelector(selector)?.classList.remove("skeleton");
}
