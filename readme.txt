======================================================================
                  NOVAKARGO - LOJISTIK YONETIM SISTEMI
======================================================================

PROJE TANIMI:
NovaKargo; kurye, admin ve musteri rollerini iceren, ucret hesaplama,
kargo takibi ve operasyonel surecleri yoneten bir web uygulamasidir.

GELISTIRICI: Yusuf Ozelci
OKUL: Karadeniz Teknik Universitesi (KTU)
BOLUM: Yazilim Muhendisligi

----------------------------------------------------------------------
1. KULLANILAN TEKNOLOJILER
----------------------------------------------------------------------
- Backend: Java 17, Spring Boot, Spring Data JPA
- Veritabani: PostgreSQL
- Frontend: HTML5, CSS3, JavaScript, Thymeleaf
- Guvenlik: Spring Security, JWT (Token tabanli yetkilendirme)
- DevOps: GitHub Actions (CI/CD), AWS Elastic Beanstalk (Deployment)

----------------------------------------------------------------------
2. PROJE OZELLIKLERI
----------------------------------------------------------------------
- Katmanli Mimari (Controller, Service, Repository) yapisi kullanilmistir.
- Global Exception Handling ile merkezi hata yonetimi saglanmistir.
- JWT Filter ile API guvenligi ve yetkilendirme katmani olusturulmustur.
- Kargo ucretleri mesafe ve agirliga gore dinamik hesaplanmaktir.
- Kuryeler icin teslim edilmis kargoyu geri alma engeli (Business Logic) eklenmistir.
- Kayit ekraninda 10 haneli telefon numarasi dogrulamasi (Validation) yapilmaktir.

----------------------------------------------------------------------
3. KURULUM VE CALISTIRMA (LOCALHOST)
----------------------------------------------------------------------
1) PostgreSQL'de 'kargo_db' adinda bir veritabani olusturun.
2) 'src/main/resources/application.properties' dosyasindaki
   veritabani kullanici adi ve sifresini kendi bilgilerinizle guncelleyin.
3) Terminalde projenin ana dizinine gidin.
4) 'mvn clean install' komutu ile projeyi derleyin.
5) 'mvn spring-boot:run' komutu ile uygulamayi baslatin.
6) Tarayicidan 'http://localhost:8080' adresine gidin.

----------------------------------------------------------------------
4. CANLI ORTAM VE DEPLOYMENT
----------------------------------------------------------------------
- Uygulama AWS Elastic Beanstalk uzerinde canli olarak calismaktadir.
- GitHub Actions araciligiyla CI/CD pipeline kurulmustur; development
  branch'ine yapilan her push otomatik olarak AWS'ye deploy edilmektedir.

----------------------------------------------------------------------
NOT: Projenin gorsel sunumu ve detayli dokumantasyonu icin lutfen 
README.md dosyasini inceleyiniz.
======================================================================
