<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>{{ $t('footer.title') }}</h3>
        <p>{{ $t('footer.subtitle') }}</p>
        <p>{{ $t('footer.description') }}</p>
      </div>
      <div class="footer-section">
        <h4>{{ $t('footer.quickLinks') }}</h4>
        <ul>
          <li><a href="/">{{ $t('footer.home') }}</a></li>
          <li><a href="/consultation">{{ $t('footer.consultation') }}</a></li>
          <li><a href="/doctors">{{ $t('footer.doctors') }}</a></li>
          <li><a href="/about">{{ $t('footer.about') }}</a></li>
        </ul>
      </div>
      <div class="footer-section">
        <h4>{{ $t('footer.contactUs') }}</h4>
        <p>{{ $t('footer.serviceHotline') }}</p>
        <p>{{ $t('footer.serviceHours') }}</p>
        <p>{{ $t('footer.email') }}</p>
      </div>
      <div class="footer-section">
        <h4>{{ $t('footer.legalInfo') }}</h4>
        <ul>
          <li><a href="#">{{ $t('footer.privacyPolicy') }}</a></li>
          <li><a href="#">{{ $t('footer.termsOfService') }}</a></li>
          <li><a href="#">{{ $t('footer.medicalDisclaimer') }}</a></li>
        </ul>
      </div>
    </div>
    <div class="footer-bottom">
      <p>{{ $t('footer.copyright') }}</p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 48px 24px 24px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 32px;
  margin-bottom: 32px;
}

.footer-section h3 {
  color: #fff;
  font-size: 24px;
  margin-bottom: 16px;
}

.footer-section h4 {
  color: #fff;
  font-size: 18px;
  margin-bottom: 16px;
}

.footer-section p {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 8px;
  line-height: 1.8;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 8px;
}

.footer-section ul li a {
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  transition: color 0.3s;
}

.footer-section ul li a:hover {
  color: #fff;
}

.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  text-align: center;
}

.footer-bottom p {
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}
</style>
