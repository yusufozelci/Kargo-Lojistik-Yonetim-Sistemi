document.addEventListener("DOMContentLoaded", () => {
    const menuItems = document.querySelectorAll(".menu-item");
    const sections = document.querySelectorAll(".content-section");
    const refreshButton = document.getElementById("refreshButton");
    const shipmentSearchInput = document.getElementById("shipmentSearchInput");

    let shipments = [];
    let customers = [];
    let users = [];
    let branches = [];
    let vehicles = [];
    let routes = [];
    let addresses = [];

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
            loadBranches(),
            loadVehicles(),
            loadRoutes(),
            loadAddresses()
        ]);

        updateStats();
        renderLatestShipments();
        renderShipmentsTable(shipments);
        renderCustomersTable();
        renderUsersTable();
        renderBranchesTable();
        populateRouteDropdowns();
        renderRoutesTable();
        populateShipmentDropdowns();
    }

    async function loadAddresses() {
        try {
            const response = await fetch("/api/addresses");
            if (response.ok) addresses = await response.json();
        } catch { addresses = []; }
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

        latestShipmentsTable.innerHTML = latest.map(shipment => {
            let senderStr = shipment.senderName || (shipment.sender ? shipment.sender.fullName : "-");
            let receiverStr = shipment.receiverName || (shipment.receiver ? shipment.receiver.fullName : "-");

            return `
            <tr>
                <td>${escapeHtml(shipment.trackingCode)}</td>
                <td>${escapeHtml(senderStr)}</td>
                <td>${escapeHtml(receiverStr)}</td>
                <td>${renderStatusBadge(shipment.status)}</td>
                <td>${formatCurrency(shipment.totalPrice)}</td>
            </tr>
            `;
        }).join("");
    }

    function renderShipmentsTable(data) {
        const tbody = document.getElementById("shipmentsTable");
        if (!tbody) return;

        if (!data || data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6">Kargo bulunamadı.</td></tr>`;
            return;
        }

        const couriers = users.filter(u => {
            let roleStr = u.roleName || (u.role ? u.role.roleName : "");
            return roleStr && roleStr.includes("COURIER");
        });
        let courierOptions = `<option value="">Kurye Seç</option>`;
        couriers.forEach(c => {
            courierOptions += `<option value="${c.id}">${c.fullName}</option>`;
        });

        tbody.innerHTML = data.map(shipment => {
            let senderStr = shipment.senderName || (shipment.sender ? shipment.sender.fullName : "-");
            let receiverStr = shipment.receiverName || (shipment.receiver ? shipment.receiver.fullName : "-");

            return `
            <tr>
                <td><strong>${shipment.trackingCode}</strong></td>
                <td>${escapeHtml(senderStr)}</td>
                <td>${escapeHtml(receiverStr)}</td>
                <td>${renderStatusBadge(shipment.status)}</td>
                <td>${formatCurrency(shipment.totalPrice)}</td>
                <td style="min-width: 200px;">
                    <div style="display: flex; flex-direction: column; gap: 8px;">
                        
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <select id="status-select-${shipment.id}" style="padding: 6px; border-radius: 6px; border: 1px solid var(--border); outline: none; flex: 1; font-size: 12px;">
                                <option value="PENDING" ${shipment.status === 'PENDING' ? 'selected' : ''}>Beklemede</option>
                                <option value="IN_TRANSIT" ${shipment.status === 'IN_TRANSIT' ? 'selected' : ''}>Yolda</option>
                                <option value="DELIVERED" ${shipment.status === 'DELIVERED' ? 'selected' : ''}>Teslim Edildi</option>
                                <option value="CANCELLED" ${shipment.status === 'CANCELLED' ? 'selected' : ''}>İptal</option>
                            </select>
                            <button class="primary-button" style="height: 28px; padding: 0 8px; font-size: 11px; margin: 0;" 
                                    onclick="updateShipmentStatus(${shipment.id})">Güncelle</button>
                        </div>

                        <div style="display: flex; gap: 8px; align-items: center;">
                            <select id="courier-select-${shipment.id}" style="padding: 6px; border-radius: 6px; border: 1px solid var(--border); outline: none; flex: 1; font-size: 12px;">
                                ${courierOptions}
                            </select>
                            <button style="height: 28px; padding: 0 8px; font-size: 11px; margin: 0; background: var(--border); color: var(--navy); border: none; border-radius: 6px; font-weight: bold; cursor: pointer;" 
                                    onclick="assignCourier(${shipment.id})">Ata</button>
                        </div>
                        
                        <button style="height: 28px; margin-top: 4px; width: 100%; border-radius: 6px; border: 1px solid var(--orange); background: transparent; color: var(--orange); font-size: 11px; font-weight: bold; cursor: pointer;" 
                                onclick="viewTrackingHistory('${shipment.trackingCode}')">Geçmişi Gör</button>

                    </div>
                </td>
            </tr>
            `;
        }).join("");
    }

    function renderCustomersTable() {
        const customersTable = document.getElementById("customersTable");

        if (!customers || customers.length === 0) {
            customersTable.innerHTML = `<tr><td colspan="5">Müşteri kaydı bulunamadı.</td></tr>`;
            return;
        }

        customersTable.innerHTML = customers.map(customer => `
            <tr>
                <td>${customer.id}</td>
                <td>${escapeHtml(customer.fullName)}</td>
                <td>${escapeHtml(customer.phone || "-")}</td>
                <td>${escapeHtml(customer.customerType || "-")}</td>
                <td>
                    <button class="btn-small btn-delete" onclick="deleteCustomer(${customer.id})">Sil</button>
                </td>
            </tr>
        `).join("");
    }

    function renderUsersTable() {
        const usersTable = document.getElementById("usersTable");

        if (!usersTable) return;

        if (!users || users.length === 0) {
            usersTable.innerHTML = `<tr><td colspan="7">Kullanıcı kaydı bulunamadı.</td></tr>`;
            return;
        }

        usersTable.innerHTML = users.map(user => {
            let roleStr = user.roleName || (user.role ? user.role.roleName : "-");

            return `
            <tr>
                <td>${user.id}</td>
                <td>${escapeHtml(user.fullName)}</td>
                <td>${escapeHtml(user.email)}</td>
                <td>${escapeHtml(user.phone || "-")}</td>
                <td>${renderRoleBadge(roleStr)}</td>
                <td>${renderUserStatus(user.status)}</td>
                <td>
                    <button class="btn-small btn-delete" onclick="deleteUser(${user.id})">Sil</button>
                </td>
            </tr>
            `;
        }).join("");
    }

    function renderBranchesTable() {
        const branchesTable = document.getElementById("branchesTable");

        if (!branches || branches.length === 0) {
            branchesTable.innerHTML = `<tr><td colspan="5">Şube kaydı bulunamadı.</td></tr>`;
            return;
        }

        branchesTable.innerHTML = branches.map(branch => `
        <tr>
            <td>${branch.id}</td>
            <td>${escapeHtml(branch.name)}</td>
            <td>${escapeHtml(branch.cityName || "-")}</td>
            <td>${branch.isTransferCenter ? "Evet" : "Hayır"}</td>
            <td>
                <button class="btn-small btn-delete" onclick="deleteBranch(${branch.id})">Sil</button>
            </td>
        </tr>
    `).join("");
    }



    function filterShipments(searchValue) {
        const query = searchValue.toLowerCase().trim();

        if (!query) {
            return shipments;
        }

        return shipments.filter(shipment => {
            let senderStr = shipment.senderName || (shipment.sender ? shipment.sender.fullName : "");
            let receiverStr = shipment.receiverName || (shipment.receiver ? shipment.receiver.fullName : "");

            return String(shipment.trackingCode || "").toLowerCase().includes(query) ||
                String(senderStr).toLowerCase().includes(query) ||
                String(receiverStr).toLowerCase().includes(query) ||
                String(shipment.status || "").toLowerCase().includes(query);
        });
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
        let cleanRole = String(roleName || "-").replace("ROLE_", "");

        const roleText = {
            ADMIN: "Admin",
            CUSTOMER: "Kullanıcı",
            USER: "Kullanıcı",
            COURIER: "Kurye"
        };

        let displayText = roleText[cleanRole] || escapeHtml(cleanRole);
        let badgeColor = displayText === "-" ? "status-cancelled" : "status-in-transit";

        return `
        <span class="status-badge ${badgeColor}">
            ${displayText}
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

    async function loadVehicles() {
        try {
            const response = await fetch("/api/vehicles");
            if (response.ok) vehicles = await response.json();
        } catch { vehicles = []; }
    }

    async function loadRoutes() {
        try {
            const response = await fetch("/api/routes");
            if (response.ok) routes = await response.json();
        } catch { routes = []; }
    }

    function populateRouteDropdowns() {
        const vehicleSelect = document.getElementById("vehicleSelect");
        const depSelect = document.getElementById("departureBranch");
        const arrSelect = document.getElementById("arrivalBranch");

        if (vehicleSelect) {
            vehicleSelect.innerHTML = `<option value="">Araç Seçin</option>` +
                vehicles.map(v => `<option value="${v.id}">${v.plateNumber} (${v.vehicleType})</option>`).join("");
        }

        const branchOptions = `<option value="">Şube Seçin</option>` +
            branches.map(b => `<option value="${b.id}">${b.name}</option>`).join("");

        if (depSelect) depSelect.innerHTML = branchOptions;
        if (arrSelect) arrSelect.innerHTML = branchOptions;
    }

    function renderRoutesTable() {
        const routesTable = document.getElementById("routesTable");
        if (!routesTable) return;

        if (!routes || routes.length === 0) {
            routesTable.innerHTML = `<tr><td colspan="5">Aktif rota bulunamadı.</td></tr>`;
            return;
        }

        routesTable.innerHTML = routes.map(route => `
            <tr>
                <td>${route.id}</td>
                <td>${route.vehicle ? route.vehicle.plateNumber : "-"}</td>
                <td>${route.departureBranch ? route.departureBranch.name : "-"} ➔ ${route.arrivalBranch ? route.arrivalBranch.name : "-"}</td>
                <td>${new Date(route.departureTime).toLocaleString('tr-TR')}</td>
                <td><button class="btn-small" onclick="openLoadShipmentModal(${route.id})">Kargo Yükle</button></td>
            </tr>
        `).join("");
    }

    const createRouteForm = document.getElementById("createRouteForm");
    if (createRouteForm) {
        createRouteForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const payload = {
                vehicleId: document.getElementById("vehicleSelect").value,
                departureBranchId: document.getElementById("departureBranch").value,
                arrivalBranchId: document.getElementById("arrivalBranch").value,
                departureTime: document.getElementById("departureTime").value,
                arrivalTime: document.getElementById("arrivalTime").value || null
            };

            try {
                const response = await fetch("/api/routes", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) throw new Error("Rota oluşturulamadı.");

                showMessage("Rota başarıyla oluşturuldu.", "success");
                createRouteForm.reset();
                await loadDashboardData();
            } catch (error) {
                showMessage(error.message, "error");
            }
        });
    }

    window.assignCourier = async function(shipmentId) {
        const selectEl = document.getElementById(`courier-select-${shipmentId}`);
        const courierId = selectEl.value;

        if (!courierId) {
            showMessage("Lütfen atamak için bir kurye seçin.", "error");
            return;
        }

        try {
            const response = await fetch(`/api/shipments/${shipmentId}/kurye-ata/${courierId}`, {
                method: "PUT"
            });

            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage || "Kurye ataması sırasında bir hata oluştu.");
            }

            showMessage("Kurye başarıyla atandı ve kargo yola çıkmaya hazır!", "success");
            await loadDashboardData();

        } catch (error) {
            showMessage(error.message, "error");
        }
    };

    window.updateShipmentStatus = async function(shipmentId) {
        const selectEl = document.getElementById(`status-select-${shipmentId}`);
        const newStatus = selectEl.value;

        try {
            const response = await fetch(`/api/shipments/${shipmentId}/durum?yeniDurum=${newStatus}`, {
                method: "PUT"
            });

            if (!response.ok) {
                throw new Error("Durum güncellenemedi (Teslim edilmiş kargo değiştirilemez).");
            }

            showMessage("Kargo durumu başarıyla güncellendi!", "success");
            await loadDashboardData();
        } catch (error) {
            showMessage(error.message, "error");
        }
    };

    window.viewTrackingHistory = async function(trackingCode) {
        try {
            const response = await fetch(`/api/tracking/${trackingCode}`);
            if (!response.ok) throw new Error("Geçmiş alınamadı.");

            const history = await response.json();

            document.getElementById("modalTrackingCode").textContent = trackingCode;
            const timeline = document.getElementById("trackingTimeline");

            if (history.length === 0) {
                timeline.innerHTML = "<li><div class='timeline-desc'>Henüz bir hareket kaydı bulunmuyor.</div></li>";
            } else {
                timeline.innerHTML = history.map(log => `
                    <li>
                        <span class="timeline-date">${new Date(log.dateTime).toLocaleString('tr-TR')}</span>
                        <div class="timeline-desc"><strong>${log.status}:</strong> ${escapeHtml(log.description)}</div>
                    </li>
                `).join("");
            }

            document.getElementById("trackingModal").classList.remove("hidden");
        } catch (error) {
            showMessage(error.message, "error");
        }
    };

    const closeModalBtn = document.getElementById("closeModalBtn");
    if (closeModalBtn) {
        closeModalBtn.addEventListener("click", () => {
            document.getElementById("trackingModal").classList.add("hidden");
        });
    }

    const trackingModal = document.getElementById("trackingModal");
    if (trackingModal) {
        trackingModal.addEventListener("click", (e) => {
            if (e.target === trackingModal) {
                trackingModal.classList.add("hidden");
            }
        });
    }

    function populateShipmentDropdowns() {
        const senderSelect = document.getElementById("senderSelect");
        const receiverSelect = document.getElementById("receiverSelect");
        const originSelect = document.getElementById("originAddressSelect");
        const destSelect = document.getElementById("destinationAddressSelect");
        const branchAddressSelect = document.getElementById("branchAddressId");

        const addressOptions = `<option value="">Seçiniz...</option>` +
            addresses.map(a => `<option value="${a.id}">${a.title} - ${a.city}</option>`).join("");

        if (senderSelect) {
            const customerOptions = `<option value="">Seçiniz...</option>` +
                customers.map(c => `<option value="${c.id}">${c.fullName} (${c.phone})</option>`).join("");
            senderSelect.innerHTML = customerOptions;
            receiverSelect.innerHTML = customerOptions;
        }

        if (originSelect) originSelect.innerHTML = addressOptions;
        if (destSelect) destSelect.innerHTML = addressOptions;
        if (branchAddressSelect) branchAddressSelect.innerHTML = addressOptions;
    }

    const createShipmentForm = document.getElementById("createShipmentForm");
    if (createShipmentForm) {
        createShipmentForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const payload = {
                senderId: document.getElementById("senderSelect").value,
                receiverId: document.getElementById("receiverSelect").value,
                originAddressId: document.getElementById("originAddressSelect").value,
                destinationAddressId: document.getElementById("destinationAddressSelect").value,
                weight: parseFloat(document.getElementById("shipmentWeight").value),
                distance: parseFloat(document.getElementById("shipmentDistance").value)
            };

            try {
                const response = await fetch("/api/shipments/ekle", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) throw new Error("Kargo oluşturulamadı. Lütfen verileri kontrol edin.");

                const result = await response.json();
                showMessage(`Kargo başarıyla oluşturuldu! Takip Kodu: ${result.trackingCode}`, "success");
                createShipmentForm.reset();
                await loadDashboardData();
            } catch (error) {
                showMessage(error.message, "error");
            }
        });
    }


    window.openQuickCustomerModal = function() {
        document.getElementById("quickCustomerModal").classList.remove("hidden");
    };

    const closeCustomerModalBtn = document.getElementById("closeCustomerModalBtn");
    if (closeCustomerModalBtn) {
        closeCustomerModalBtn.addEventListener("click", () => {
            document.getElementById("quickCustomerModal").classList.add("hidden");
        });
    }

    const quickCustomerForm = document.getElementById("quickCustomerForm");
    if (quickCustomerForm) {
        quickCustomerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            try {
                const customerPayload = {
                    fullName: document.getElementById("qcName").value,
                    phone: document.getElementById("qcPhone").value,
                    customerType: document.getElementById("qcType").value
                };

                const customerRes = await fetch("/api/customers", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(customerPayload)
                });

                if (!customerRes.ok) throw new Error("Müşteri kaydedilemedi.");
                const newCustomer = await customerRes.json();

                const addressPayload = {
                    customerId: newCustomer.id,
                    title: document.getElementById("qcAddressTitle").value,
                    city: document.getElementById("qcCity").value,
                    addressText: document.getElementById("qcAddressText").value
                };

                const addressRes = await fetch("/api/addresses", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(addressPayload)
                });

                if (!addressRes.ok) throw new Error("Müşteri kaydedildi ancak adres eklenemedi.");

                showMessage("Müşteri ve adres başarıyla sisteme tanımlandı!", "success");

                await Promise.all([loadCustomers(), loadAddresses()]);
                populateShipmentDropdowns();

                quickCustomerForm.reset();
                document.getElementById("quickCustomerModal").classList.add("hidden");

            } catch (error) {
                showMessage(error.message, "error");
            }
        });
    }


    window.openCreateBranchModal = function() {
        document.getElementById("createBranchModal").classList.remove("hidden");
    };

    const closeBranchModalBtn = document.getElementById("closeBranchModalBtn");
    if (closeBranchModalBtn) {
        closeBranchModalBtn.addEventListener("click", () => {
            document.getElementById("createBranchModal").classList.add("hidden");
        });
    }

    const createBranchForm = document.getElementById("createBranchForm");
    if (createBranchForm) {
        createBranchForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            try {
                const addressPayload = {
                    title: document.getElementById("branchName").value + " Merkezi",
                    city: document.getElementById("branchCity").value,
                    addressText: document.getElementById("branchAddressText").value
                };

                const addressRes = await fetch("/api/addresses", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(addressPayload)
                });

                if (!addressRes.ok) throw new Error("Şubenin fiziksel adresi kaydedilemedi.");
                const savedAddress = await addressRes.json();


                const branchPayload = {
                    name: document.getElementById("branchName").value,
                    cityName: document.getElementById("branchCity").value,
                    isTransferCenter: document.getElementById("isTransferCenter").value === "true",
                    addressId: savedAddress.id
                };

                const branchRes = await fetch("/api/branches", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(branchPayload)
                });

                if (!branchRes.ok) throw new Error("Şube adresi eklendi ancak şube kaydı başarısız oldu.");

                showMessage("Yeni şube ve adresi başarıyla sisteme eklendi!", "success");

                await loadBranches();
                renderBranchesTable();
                populateRouteDropdowns();

                createBranchForm.reset();
                document.getElementById("createBranchModal").classList.add("hidden");

            } catch (error) {
                showMessage(error.message, "error");
            }
        });
    }

    window.deleteBranch = async function(id) {
        if (!confirm("Bu şubeyi silmek istediğinize emin misiniz?")) return;

        try {
            const response = await fetch(`/api/branches/${id}`, {
                method: "DELETE"
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Şube silinirken bir hata oluştu.");
            }

            showMessage("Şube başarıyla silindi.", "success");

            await loadBranches();
            renderBranchesTable();
            populateRouteDropdowns();

        } catch (error) {
            showMessage(error.message, "error");
        }
    };

    window.deleteUser = async function(id) {
        if (!confirm("Bu kullanıcıyı sistemden silmek istediğinize emin misiniz?")) return;

        try {
            const response = await fetch(`/api/users/${id}`, {
                method: "DELETE"
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Kullanıcı silinirken bir hata oluştu.");
            }

            showMessage("Kullanıcı başarıyla silindi.", "success");

            await loadUsers();
            renderUsersTable();

        } catch (error) {
            showMessage(error.message, "error");
        }
    };

    window.deleteCustomer = async function(id) {
        if (!confirm("Bu müşteriyi silmek istediğinize emin misiniz? (Müşterinin geçmiş kargo kayıtları korunacaktır.)")) return;

        try {
            const response = await fetch(`/api/customers/${id}`, {
                method: "DELETE"
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Müşteri silinirken bir hata oluştu.");
            }

            showMessage("Müşteri başarıyla silindi.", "success");

            await loadCustomers();
            renderCustomersTable();
            populateShipmentDropdowns();

        } catch (error) {
            showMessage(error.message, "error");
        }
    };

    window.openLoadShipmentModal = function(routeId) {
        document.getElementById("targetRouteId").innerText = routeId;
        document.getElementById("hiddenRouteId").value = routeId;

        const pendingShipments = shipments.filter(s => s.status === "PENDING");
        const select = document.getElementById("pendingShipmentSelect");

        select.innerHTML = '<option value="">Yüklenecek Kargoyu Seçin...</option>';
        pendingShipments.forEach(s => {
            select.innerHTML += `<option value="${s.id}">${s.trackingCode} - (${s.sender ? s.sender.fullName : '-'} -> ${s.receiver ? s.receiver.fullName : '-'})</option>`;
        });

        document.getElementById("loadShipmentModal").classList.remove("hidden");
    };

    document.getElementById("closeLoadModalBtn").addEventListener("click", () => {
        document.getElementById("loadShipmentModal").classList.add("hidden");
    });

    document.getElementById("loadShipmentForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const routeId = document.getElementById("hiddenRouteId").value;
        const shipmentId = document.getElementById("pendingShipmentSelect").value;

        try {
            const response = await fetch(`/api/routes/${routeId}/load-shipment/${shipmentId}`, {
                method: "POST"
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || "Yükleme başarısız oldu.");
            }

            showMessage("Kargo başarıyla yüklendi ve yola çıktı!", "success");

            await loadShipments();
            await loadRoutes();
            renderShipmentsTable(shipments);
            renderRoutesTable();

            document.getElementById("loadShipmentModal").classList.add("hidden");
        } catch (error) {
            showMessage(error.message, "error");
        }
    });


    window.openCreateVehicleModal = function() {
        document.getElementById("createVehicleModal").classList.remove("hidden");
    };

    const closeVehicleModalBtn = document.getElementById("closeVehicleModalBtn");
    if (closeVehicleModalBtn) {
        closeVehicleModalBtn.addEventListener("click", () => {
            document.getElementById("createVehicleModal").classList.add("hidden");
        });
    }

    const createVehicleForm = document.getElementById("createVehicleForm");
    if (createVehicleForm) {
        createVehicleForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const payload = {
                plateNumber: document.getElementById("plateNumber").value,
                vehicleType: document.getElementById("vehicleType").value,
                capacity: parseFloat(document.getElementById("capacity").value)
            };

            try {
                const res = await fetch("/api/vehicles", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                if (!res.ok) throw new Error("Araç kaydedilemedi.");

                showMessage("Yeni araç başarıyla filoya eklendi!", "success");

                await loadVehicles();
                populateRouteDropdowns();

                createVehicleForm.reset();
                document.getElementById("createVehicleModal").classList.add("hidden");

            } catch (error) {
                showMessage(error.message, "error");
            }
        });
    }

});