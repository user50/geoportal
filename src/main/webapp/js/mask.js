function showMask() {
    var mask = document.getElementById('loadmask');
    var img = document.getElementById('loadimg');
    mask.style.display = 'block';
    wWidth = (('innerWidth' in window) ? window.innerWidth : document.documentElement.clientWidth);
    wHeight = (('innerHeight' in window) ? window.innerHeight
            : document.documentElement.clientHeight);
    mask.style.height = wHeight + 'px';
    mask.style.width = wWidth + 'px';
    img.style.left = wWidth / 2 + 'px';
    img.style.top = wHeight / 2 + 'px';
}

function hideMask() {
    var mask = document.getElementById('loadmask');
    mask.style.display = 'none';
}