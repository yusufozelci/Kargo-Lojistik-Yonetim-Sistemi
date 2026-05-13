document.addEventListener("DOMContentLoaded", () => {
    loadMyShipments();
});

async function loadMyShipments() {
    try {
        const response = await fetch("/api/courier/my-shipments");
        if (!response.ok) throw new Error("Kargo verileri alınamadı.");

        const shipments = await response.json();
        renderShipments(shipments);
    } catch (error) {
        showMessage(error.message, "error");
    }
}

function renderShipments(shipments) {
    const tbody = document.getElementById("courierShipmentsTable");

    if (!shipments || shipments.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 20px;">Üzerinize zimmetli kargo bulunmuyor. Harika bir gün! 🎉</td></tr>`;
        return;
    }

    tbody.innerHTML = shipments.map(s => {
        const actionHtml = s.status === 'DELIVERED'
            ? `<span style="color: #28a745; font-weight: bold; font-size: 13px; display: flex; align-items: center; gap: 5px;">
                 <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M12.736 3.97a.733.733 0 0 1 1.047 0c.286.289.29.756.01 1.05L7.88 12.01a.733.733 0 0 1-1.065.02L3.217 8.384a.757.757 0 0 1 0-1.06.733.733 0 0 1 1.047 0l3.052 3.093 5.4-6.425a.247.247 0 0 1 .02-.022Z"/></svg>
                 Tamamlandı
               </span>`
            : `<div style="display: flex; gap: 8px; align-items: center;">
                <select id="status-${s.id}" style="padding: 6px; border-radius: 6px; border: 1px solid var(--border); outline: none; font-size: 13px;">
                    <option value="IN_TRANSIT" ${s.status === 'IN_TRANSIT' ? 'selected' : ''}>Yolda (Dağıtımda)</option>
                    <option value="DELIVERED">Teslim Edildi</option>
                </select>
                <button class="primary-button" style="height: 30px; padding: 0 12px; font-size: 12px; margin: 0;" 
                        onclick="updateShipmentStatus(${s.id})">Uygula</button>
               </div>`;

        return `
        <tr>
            <td><strong>${escapeHtml(s.trackingCode)}</strong></td>
            <td>${escapeHtml(s.sender ? s.sender.fullName : '-')}</td>
            <td>
                <strong>${escapeHtml(s.receiver ? s.receiver.fullName : '-')}</strong><br>
                <small style="color: var(--muted);">${escapeHtml(s.destinationAddress ? s.destinationAddress.fullAddress : '')}</small><br>
                <small style="color: var(--orange); font-weight: bold;">${escapeHtml(s.destinationAddress ? s.destinationAddress.city : '')}</small>
            </td>
            <td>${renderStatusBadge(s.status)}</td>
            <td>${actionHtml}</td>
        </tr>
        `;
    }).join("");
}

window.updateShipmentStatus = async function(id) {
    const newStatus = document.getElementById(`status-${id}`).value;

    try {
        const response = await fetch(`/api/courier/shipments/${id}/status?status=${newStatus}`, {
            method: "PATCH"
        });

        if (!response.ok) throw new Error("Durum güncellenemedi.");

        showMessage("Kargo durumu başarıyla güncellendi!", "success");
        loadMyShipments(); // Tabloyu yenile
    } catch (error) {
        showMessage(error.message, "error");
    }
};

function renderStatusBadge(status) {
    const statusText = {
        PENDING: "Beklemede",
        IN_TRANSIT: "Dağıtımda",
        DELIVERED: "Teslim Edildi",
        CANCELLED: "İptal Edildi"
    };

    const statusClass = {
        PENDING: "status-pending",
        IN_TRANSIT: "status-transit",
        DELIVERED: "status-delivered",
        CANCELLED: "status-cancelled"
    };

    return `<span class="status-badge ${statusClass[status] || "status-pending"}">${statusText[status] || status}</span>`;
}

function showMessage(message, type) {
    const msgBox = document.getElementById("dashboardMessage");
    msgBox.classList.remove("hidden", "success", "error");
    msgBox.classList.add(type);
    msgBox.textContent = message;
    setTimeout(() => msgBox.classList.add("hidden"), 3000);
}

function escapeHtml(value) {
    return String(value || "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;").replaceAll("'", "&#039;");
}