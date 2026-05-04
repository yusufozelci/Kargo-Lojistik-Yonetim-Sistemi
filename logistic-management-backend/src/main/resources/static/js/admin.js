function fetchOperationsData() {
    fetch('/api/shipments')
        .then(res => res.json())
        .then(data => {
            document.getElementById('stat-pending').innerText = data.filter(s => s.status === 'PENDING').length;
            document.getElementById('stat-transit').innerText = data.filter(s => s.status === 'IN_TRANSIT').length;

            const listWrapper = document.getElementById('shipment-list');
            listWrapper.innerHTML = '';

            const recent = data.slice(-8).reverse();

            if(recent.length === 0) {
                listWrapper.innerHTML = '<div style="text-align:center; padding:20px; color:#9ca3af;">Sistemde aktif gönderi yok.</div>';
                return;
            }

            recent.forEach(s => {
                let pillClass = 'bg-pending';
                let statusText = 'İşlemde';

                if(s.status === 'IN_TRANSIT') { pillClass = 'bg-transit'; statusText = 'Dağıtımda'; }
                if(s.status === 'DELIVERED') { pillClass = 'bg-delivered'; statusText = 'Teslim Edildi'; }

                const price = s.totalPrice ? s.totalPrice + ' ₺' : 'Hesaplanıyor...';

                listWrapper.innerHTML += `
                    <div class="list-item">
                        <div class="item-info">
                            <h5>${s.trackingCode}</h5>
                            <p>${s.senderName} <span>&rarr;</span> ${s.receiverName} • <strong>${price}</strong></p>
                        </div>
                        <div class="status-pill ${pillClass}">${statusText}</div>
                    </div>
                `;
            });
        });
}

function loadCustomers() {
    fetch('/api/customers')
        .then(res => res.json())
        .then(data => {
            const senderSelect = document.getElementById('senderId');
            const receiverSelect = document.getElementById('receiverId');

            senderSelect.innerHTML = '<option value="" disabled selected>Gönderici Seçin...</option>';
            receiverSelect.innerHTML = '<option value="" disabled selected>Alıcı Seçin...</option>';

            data.forEach(customer => {
                const optionHtml = `<option value="${customer.id}">${customer.fullName} (${customer.phone || 'Tel Yok'})</option>`;
                senderSelect.innerHTML += optionHtml;
                receiverSelect.innerHTML += optionHtml;
            });
        })
        .catch(err => console.error("Müşteriler çekilirken hata oluştu:", err));
}

function loadAddresses() {
    fetch('/api/addresses')
        .then(res => res.json())
        .then(data => {
            const originSelect = document.getElementById('originId');
            const destSelect = document.getElementById('destId');

            originSelect.innerHTML = '<option value="" disabled selected>Çıkış Adresi Seçin...</option>';
            destSelect.innerHTML = '<option value="" disabled selected>Varış Adresi Seçin...</option>';

            data.forEach(address => {
                const optionText = `${address.title} - ${address.city}/${address.district} (${address.customerName})`;
                const optionHtml = `<option value="${address.id}">${optionText}</option>`;

                originSelect.innerHTML += optionHtml;
                destSelect.innerHTML += optionHtml;
            });
        })
        .catch(err => console.error("Adresler çekilirken hata oluştu:", err));
}



document.getElementById('quickShipmentForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const alertBox = document.getElementById('form-alert');
    alertBox.innerText = 'İşleniyor...';
    alertBox.style.color = '#6b7280';

    const payload = {
        senderId: document.getElementById('senderId').value,
        receiverId: document.getElementById('receiverId').value,
        originAddressId: document.getElementById('originId').value,
        destinationAddressId: document.getElementById('destId').value,
        weight: document.getElementById('weight').value,
        distance: document.getElementById('distance').value
    };

    fetch('/api/shipments/ekle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if(!response.ok) throw new Error('Hata! Adres ID numaralarının veritabanında var olduğundan emin olun.');
            return response.json();
        })
        .then(data => {
            alertBox.innerText = `Kargo eklendi! Takip Kodu: ${data.trackingCode}`;
            alertBox.style.color = '#059669';
            document.getElementById('quickShipmentForm').reset();
            fetchOperationsData();
        })
        .catch(err => {
            alertBox.innerText = err.message;
            alertBox.style.color = '#dc2626';
        });
});

document.addEventListener('DOMContentLoaded', () => {
    fetchOperationsData();
    loadCustomers();
    loadAddresses();
});