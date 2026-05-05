(function () {
    const STORAGE_KEY = "novakargo-theme";
    const root = document.documentElement;

    function getSavedTheme() {
        return localStorage.getItem(STORAGE_KEY) || "light";
    }

    function applyTheme(theme) {
        root.setAttribute("data-theme", theme);
        localStorage.setItem(STORAGE_KEY, theme);
        updateButtons(theme);
    }

    function updateButtons(theme) {
        document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
            const icon = button.querySelector("[data-theme-icon]");
            const label = button.querySelector("[data-theme-label]");

            if (icon) icon.textContent = theme === "dark" ? "☀" : "🌙";
            if (label) label.textContent = theme === "dark" ? "Açık mod" : "Koyu mod";
            button.setAttribute("aria-label", theme === "dark" ? "Açık moda geç" : "Koyu moda geç");
        });
    }

    function createThemeButton() {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "theme-toggle";
        button.setAttribute("data-theme-toggle", "");
        button.innerHTML = '<span data-theme-icon>🌙</span><span data-theme-label>Koyu mod</span>';
        button.addEventListener("click", () => {
            const current = root.getAttribute("data-theme") || "light";
            applyTheme(current === "dark" ? "light" : "dark");
        });
        return button;
    }

    // Tema mümkün olduğunca erken uygulansın.
    root.setAttribute("data-theme", getSavedTheme());

    document.addEventListener("DOMContentLoaded", function () {
        const headerInner = document.querySelector(".header-inner");
        if (!headerInner) return;

        let actions = headerInner.querySelector(".header-actions");

        if (!actions) {
            actions = document.createElement("div");
            actions.className = "header-actions";

            const lastElement = headerInner.lastElementChild;

            // Header'daki son buton/formu aksiyon grubuna taşı. Böylece grid yapısı bozulmaz.
            if (lastElement && !lastElement.classList.contains("brand") && !lastElement.classList.contains("main-nav") && !lastElement.classList.contains("menu")) {
                headerInner.removeChild(lastElement);
                actions.appendChild(lastElement);
            }

            headerInner.appendChild(actions);
        }

        if (!actions.querySelector("[data-theme-toggle]")) {
            actions.insertBefore(createThemeButton(), actions.firstChild);
        }

        updateButtons(root.getAttribute("data-theme") || "light");
    });
})();
