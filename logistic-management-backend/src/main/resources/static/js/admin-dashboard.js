document.addEventListener("DOMContentLoaded", () => {
    const menuItems = document.querySelectorAll(".menu-item");
    const sections = document.querySelectorAll(".content-section");
    const refreshButton = document.getElementById("refreshButton");
    const shipmentSearchInput = document.getElementById("shipmentSearchInput");

    let shipments = [];
    let customers = [];
    let users = [];
    let branches = [];

    menuItems.forEach(item => {
        item.addEventListener("click", () => {
            menuItems.forEach(menuItem => menuItem.classList.remove("active"));
            item.classList.add("active");

            sections.forEach(section => section.classList.remove("active-section"));

            const targetSection = document.getElementById(item.dataset.section);
            targetSection.classList.add("active-section");
        });
    });

    refreshButton.addEventListener("click", loadDashboardData);

    shipmentSearchInput.addEventListener("input", () => {
        renderShipmentsTable(filterShipments(shipmentSearchInput.value));
    });

    loadDashboardData();

    async function loadDashboardData() {
        await Promise.all([
            loadShipments(),
            loadCustomers(),
            loadUsers(),
            loadBranches()
        ]);

        updateStats();
        renderLatestShipments();
        renderShipmentsTable(shipments);
        renderCustomersTable();
        renderUsersTable();
        renderBranchesTable();
    }

    async function loadShipments() {
        try {
            const response = await fetch("/api/shipments");

            if (!response.ok) {
                throw new Error("Kargo verileri alınamadı.");
            }

            shipments = await response.json();
        } catch (error) {
            shipments = [];
            showMessage(error.message, "error");
        }
    }

    async function loadCustomers() {
        try {
            const response = await fetch("/api/customers");

            if (!response.ok) {
                throw new Error("Müşteri verileri alınamadı.");
            }

            customers = await response.json();
        } catch {
            customers = [];
        }
    }

    async function loadUsers() {
        try {
            const response = await fetch("/api/users");

            if (!response.ok) {
                throw new Error("Kullanıcı verileri alınamadı.");
            }

            users = await response.json();
        } catch {
            users = [];
        }
    }

    async function loadBranches() {
        try {
            const response = await fetch("/api/branches");

            if (!response.ok) {
                throw new Error("Şube verileri alınamadı.");
            }

            branches = await response.json();
        } catch {
            branches = [];
        }
    }

    function updateStats() {
        document.getElementById("totalShipments").textContent = shipments.length;
        document.getElementById("pendingShipments").textContent = countByStatus("PENDING");
        document.getElementById("inTransitShipments").textContent = countByStatus("IN_TRANSIT");
        document.getElementById("deliveredShipments").textContent = countByStatus("DELIVERED");
    }

    function countByStatus(status) {
        return shipments.filter(shipment => shipment.status === status).length;
    }

    function renderLatestShipments() {
        const latestShipmentsTable = document.getElementById("latestShipmentsTable");

        const latest = shipments.slice(-5).reverse();

        if (latest.length === 0) {
            latestShipmentsTable.innerHTML = `<tr><td colspan="5">Henüz kargo kaydı yok.</td></tr>`;
            return;
        }

        latestShipmentsTable.innerHTML = latest.map(shipment => `
            <tr>
                <td>${escapeHtml(shipment.trackingCode)}</td>
                <td>${escapeHtml(shipment.senderName)}</td>
                <td>${escapeHtml(shipment.receiverName)}</td>
                <td>${renderStatusBadge(shipment.status)}</td>
                <td>${formatCurrency(shipment.totalPrice)}</td>
            </tr>
        `).join("");
    }

    function renderShipmentsTable(data) {
        const shipmentsTable = document.getElementById("shipmentsTable");

        if (!data || data.length === 0) {
            shipmentsTable.innerHTML = `<tr><td colspan="6">Kargo kaydı bulunamadı.</td></tr>`;
            return;
        }

        shipmentsTable.innerHTML = data.map(shipment => `
            <tr>
                <td>${escapeHtml(shipment.trackingCode)}</td>
                <td>${escapeHtml(shipment.senderName)}</td>
                <td>${escapeHtml(shipment.receiverName)}</td>
                <td>${renderStatusBadge(shipment.status)}</td>
                <td>${formatCurrency(shipment.totalPrice)}</td>
                <td>
                    <select class="status-select" data-id="${shipment.id}">
                        <option value="PENDING" ${shipment.status === "PENDING" ? "selected" : ""}>Beklemede</option>
                        <option value="IN_TRANSIT" ${shipment.status === "IN_TRANSIT" ? "selected" : ""}>Yolda</option>
                        <option value="DELIVERED" ${shipment.status === "DELIVERED" ? "selected" : ""}>Teslim Edildi</option>
                        <option value="CANCELLED" ${shipment.status === "CANCELLED" ? "selected" : ""}>İptal Edildi</option>
                    </select>

                    <button class="action-button" data-update-id="${shipment.id}">
                        Güncelle
                    </button>
                </td>
            </tr>
        `).join("");

        document.querySelectorAll("[data-update-id]").forEach(button => {
            button.addEventListener("click", () => {
                const shipmentId = button.dataset.updateId;
                const select = document.querySelector(`.status-select[data-id="${shipmentId}"]`);
                updateShipmentStatus(shipmentId, select.value);
            });
        });
    }

    function renderCustomersTable() {
        const customersTable = document.getElementById("customersTable");

        if (!customers || customers.length === 0) {
            customersTable.innerHTML = `<tr><td colspan="4">Müşteri kaydı bulunamadı.</td></tr>`;
            return;
        }

        customersTable.innerHTML = customers.map(customer => `
            <tr>
                <td>${customer.id}</td>
                <td>${escapeHtml(customer.fullName)}</td>
                <td>${escapeHtml(customer.phone || "-")}</td>
                <td>${escapeHtml(customer.customerType || "-")}</td>
            </tr>
        `).join("");
    }

    function renderUsersTable() {
        const usersTable = document.getElementById("usersTable");

        if (!usersTable) {
            return;
        }

        if (!users || users.length === 0) {
            usersTable.innerHTML = `<tr><td colspan="6">Kullanıcı kaydı bulunamadı.</td></tr>`;
            return;
        }

        usersTable.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${escapeHtml(user.fullName)}</td>
            <td>${escapeHtml(user.email)}</td>
            <td>${escapeHtml(user.phone || "-")}</td>
            <td>${renderRoleBadge(user.roleName)}</td>
            <td>${renderUserStatus(user.status)}</td>
        </tr>
    `).join("");
    }

    function renderBranchesTable() {
        const branchesTable = document.getElementById("branchesTable");

        if (!branches || branches.length === 0) {
            branchesTable.innerHTML = `<tr><td colspan="4">Şube kaydı bulunamadı.</td></tr>`;
            return;
        }

        branchesTable.innerHTML = branches.map(branch => `
            <tr>
                <td>${branch.id}</td>
                <td>${escapeHtml(branch.name)}</td>
                <td>${escapeHtml(branch.cityName || "-")}</td>
                <td>${branch.isTransferCenter ? "Evet" : "Hayır"}</td>
            </tr>
        `).join("");
    }

    async function updateShipmentStatus(shipmentId, status) {
        try {
            const response = await fetch(`/api/shipments/${shipmentId}/durum?yeniDurum=${status}`, {
                method: "PUT"
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Kargo durumu güncellenemedi.");
            }

            showMessage("Kargo durumu başarıyla güncellendi.", "success");
            await loadDashboardData();

        } catch (error) {
            showMessage(cleanErrorMessage(error.message), "error");
        }
    }

    function filterShipments(searchValue) {
        const query = searchValue.toLowerCase().trim();

        if (!query) {
            return shipments;
        }

        return shipments.filter(shipment =>
            String(shipment.trackingCode || "").toLowerCase().includes(query) ||
            String(shipment.senderName || "").toLowerCase().includes(query) ||
            String(shipment.receiverName || "").toLowerCase().includes(query) ||
            String(shipment.status || "").toLowerCase().includes(query)
        );
    }

    function renderStatusBadge(status) {
        const statusText = {
            PENDING: "Beklemede",
            IN_TRANSIT: "Yolda",
            DELIVERED: "Teslim Edildi",
            CANCELLED: "İptal Edildi"
        };

        const statusClass = {
            PENDING: "status-pending",
            IN_TRANSIT: "status-in-transit",
            DELIVERED: "status-delivered",
            CANCELLED: "status-cancelled"
        };

        return `
            <span class="status-badge ${statusClass[status] || "status-pending"}">
                ${statusText[status] || status}
            </span>
        `;
    }

    function renderRoleBadge(roleName) {
        const roleText = {
            ADMIN: "Admin",
            CUSTOMER: "Kullanıcı",
            COURIER: "Kurye"
        };

        return `
        <span class="status-badge status-in-transit">
            ${roleText[roleName] || escapeHtml(roleName || "-")}
        </span>
    `;
    }

    function renderUserStatus(status) {
        if (status === true) {
            return `<span class="status-badge status-delivered">Aktif</span>`;
        }

        return `<span class="status-badge status-cancelled">Pasif</span>`;
    }

    function showMessage(message, type) {
        const dashboardMessage = document.getElementById("dashboardMessage");

        dashboardMessage.classList.remove("hidden", "success", "error");
        dashboardMessage.classList.add(type);
        dashboardMessage.textContent = message;

        setTimeout(() => {
            dashboardMessage.classList.add("hidden");
        }, 4000);
    }

    function formatCurrency(value) {
        if (value === null || value === undefined || isNaN(value)) {
            return "-";
        }

        return new Intl.NumberFormat("tr-TR", {
            style: "currency",
            currency: "TRY"
        }).format(value);
    }

    function cleanErrorMessage(message) {
        if (!message) {
            return "İşlem sırasında bir hata oluştu.";
        }

        try {
            const parsed = JSON.parse(message);

            if (typeof parsed === "object") {
                return Object.values(parsed).join(" ");
            }
        } catch {
            return message;
        }

        return message;
    }

    function escapeHtml(value) {
        return String(value || "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }
});