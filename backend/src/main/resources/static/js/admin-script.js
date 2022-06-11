function toggleCollapse(menuId) {
    document.getElementById(`menu${menuId}`).classList.toggle('collapsed')
    localStorage.setItem(`menu${menuId}`, document.getElementById(`menu${menuId}`).classList.contains('collapsed') ? 'false' : 'true');
}
