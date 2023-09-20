let menuItems;
let lastShiftPress = 0;

let searchInput;
let searchItems;
let input;
let results;
let selectedIndex = -1;

function isModalOpen() {
    return document.getElementById('search-modal').style.display !== 'none';
}

function init() {
    loadMenuFromStorage();
    rememberMenuPosition();

    document.getElementById('search').addEventListener('keyup', searchListener());
    document.getElementById('search-input').addEventListener('keyup', siteSearchListener());

    searchInput = document.querySelector("#search");

    document.addEventListener("keydown", (event) => {
        const currentTime = new Date().getTime();

        // Double shift
        if (event.keyCode === 16) {
            if (currentTime - lastShiftPress < 300) {
                openSearch();
            }
            lastShiftPress = currentTime;
            event.preventDefault();

            // F2
        } else if (event.keyCode === 113) {
            searchInput.focus();
            searchInput.select();
            event.preventDefault();

            // ESC
        } else if (event.keyCode === 27) {
            if (isModalOpen()) {
                closeSearch();
                event.preventDefault();
            }
        }
    });

    input = document.getElementById('search-input');
    results = Array.from(document.querySelectorAll(".search-result a")).filter(function(result) {
        return result.style.display !== 'none';
    });

    input.addEventListener("keydown", function(event) {
        if (event.keyCode === 38) {
            event.preventDefault();
            selectedIndex = selectedIndex > 0 ? selectedIndex - 1 : results.length - 1;
            selectResult(selectedIndex);

        } else if (event.keyCode === 40 || event.keyCode === 9) {
            event.preventDefault();
            selectedIndex = selectedIndex < results.length - 1 ? selectedIndex + 1 : 0;
            selectResult(selectedIndex);

        } else if (event.keyCode === 13 && selectedIndex > -1 && isModalOpen()) {
            event.preventDefault();
            window.location.href = results[selectedIndex].href;
            closeSearch();
        }
    });
}

function selectResult(index) {
    results.forEach(function(result) {
        result.classList.remove("selected");
    });
    results[index].classList.add("selected");
    scrollToElement(results[index]);
}

function searchListener() {
    menuItems = document.querySelectorAll("#menu-results a");

    return function () {
        const searchString = this.value.toLowerCase();
        if (searchString === '')
            loadMenuFromStorage();
        else
            openAllMenuGroups();

        menuItems.forEach(function (menuItem) {
            let menuItemText = menuItem.getAttribute('data-search').toLowerCase();

            if (menuItemText.includes(searchString)) {
                menuItem.style.display = 'block';
            } else {
                menuItem.style.display = 'none';
            }
        });
    };
}

function searchFavorite() {
    openAllMenuGroups();
    document.getElementById('search').value = '';
    menuItems.forEach(function (menuItem) {
        const status = menuItem.className;

        if (status.includes('favorite')) {
            menuItem.style.display = 'block';
        } else {
            menuItem.style.display = 'none';
        }
    });
}

function searchEverything() {
    menuItems.forEach(function (menuItem) {
        menuItem.style.display = 'block';
    });
}

function clearFilter() {
    document.getElementById('search').value = '';
    searchEverything();
    loadMenuFromStorage();
}

function closeSearch() {
    document.getElementById('background-fader').style.display = 'none';
    document.getElementById('search-modal').style.display = 'none';
    let searchInput =  document.getElementById('search-input');
    searchInput.disabled = true;
}

function openSearch() {
    document.getElementById('background-fader').style.display = 'block';
    document.getElementById('search-modal').style.display = 'block';
    let searchInput =  document.getElementById('search-input');
    searchInput.disabled = false;
    searchInput.focus();
    searchInput.select();
}

function siteSearchListener() {
    const menuResults =  document.getElementById('search-results');
    searchItems = menuResults.querySelectorAll(".search-result");

    return function (event) {
        const searchString = this.value.toLowerCase();
        searchItems.forEach(function (result) {
            const siteSearchText = result.querySelector(".name").textContent.toLowerCase();
            if (event.keyCode !== 38 && event.keyCode !== 40 && event.keyCode !== 9) {
                result.querySelector("a").classList.remove('selected');
            }

            if (siteSearchText.includes(searchString)) {
                result.style.display = 'block';
            } else {
                result.style.display = 'none';
            }
        });

        if (event.keyCode !== 38 && event.keyCode !== 40 && event.keyCode !== 9) {
            selectedIndex = -1;
            results = Array.from(document.querySelectorAll(".search-result a")).filter(function(result) {
                return result.parentElement.style.display !== 'none';
            });
        }
    };
}

function scrollToElement(element) {
    console.log(element);

    if (element) {
        const offsetTop = element.offsetTop - 70;
        const scrollableElement = element.parentElement.parentElement;

        if (scrollableElement) {
            scrollableElement.scrollTo({
                top: offsetTop,
                behavior: 'instant'
            });
        }
    }
}

function closeAllMenuGroups() {
    document.querySelectorAll('.menu-group').forEach(element => {
        element.classList.add('closed');
    });
}

function openAllMenuGroups() {
    document.querySelectorAll('.menu-group').forEach(element => {
        element.classList.remove('closed');
    });
}

function saveMenuState() {
    localStorage.setItem('closedMenus', JSON.stringify(getAllClosedMenu()));
}

function toggleMenuGroup(title) {
    document.querySelector(`.menu-group[data-title="${title}"]`).classList.toggle('closed')
    if (document.getElementById('search').value === '')
        saveMenuState();
}

function loadMenuFromStorage() {
    openAllMenuGroups();
    if (!localStorage.getItem('closedMenus'))
        saveMenuState();
    JSON.parse(localStorage.getItem('closedMenus')).forEach(title => closeMenu(title));
}

function closeMenu(title) {
    let element = document.querySelector(`.menu-group[data-title="${title}"]`);
    if (element)
        element.classList.add('closed')
}

function getAllClosedMenu() {
    return Array.from(document.querySelectorAll('.menu-group[data-title]'))
        .filter(element => element.classList.contains('closed'))
        .map(element => element.getAttribute('data-title'));
}

function toggleStarred(title) {
    let element = document.querySelector(`.menu-group a[data-search='${title}']`);
    if (element) {
        toggleStar(title, () => {
            element.classList.toggle('favorite');
            document.querySelector(`span[data-star='${title}']`).classList.toggle('favorite');
        });
    }
}

function toggleStar(menu, callback) {
    fetch('/admin/api/settings/favorite', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: menu,
        credentials: 'include'
    })
        .then(result => callback())
        .catch(error => console.error(error));
}

function dismissMotd(motd) {
    fetch('/admin/api/settings/dismiss-motd', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: motd,
        credentials: 'include'
    })
        .then(result => {
            document.getElementById('motd').style.display = (motd === '' || motd === '-') ? 'inline' : 'none';
        })
        .catch(error => console.error(error));
}

function openMobileMenu() {
    document.querySelector('.menu').style.display = 'block';
}

function closeMobileMenu() {
    document.querySelector('.menu').style.display = 'none';
}

function rememberMenuPosition() {
    const menu = document.querySelector(".menu");
    const offset = Number(localStorage.getItem('menuScrollPosition')) || 0;
    menu?.scrollTo(0, isNaN(offset) ? 0 : offset);
    menu?.addEventListener("scroll", event => localStorage.setItem('menuScrollPosition', event.target.scrollTop.toString()));
}

window.onload = init;