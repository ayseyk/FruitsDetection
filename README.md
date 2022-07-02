# FruitsDetection //CekipAL Mobile Application
Bu repo derin öğrenme ile meyvelerin tazelik durumunun tespiti üzerine geliştirdiğimiz mobil uygulamamızın kodlarını içermektedir. Derin öğrenme kısmının kodlarını <a href="https://github.com/ayseyk/Rotten-FreshFruitDetection" target="_blank">bu</a> repoda bulabilirsiniz.

> Dünyada gıda israfı önemli derecede yüksek ve üzerine çalışılmasını gerektirecek ölçüde olduğu için bu çalışma önerilmiştir. Yapılan çalışmada gıda israfında büyük payı oluşturan meyve israfının önüne geçmek adına teknoloji ile tarımı birleştiren bir yaklaşım önerilmektedir. Bu doğrultuda 16 sınıftan 3200 sayıda fotoğraf içeren bir veri seti oluşturulmuştur. Bu veri setinin, VGG16, InceptionV3, Xception ve MobileNet modellerinin çeşitli epoch ve batch size kombinasyonları ile eğitilmiştir. Eğitim sonucunda en iyi başarı oranına sahip model %94 ile MobileNet modelidir. Bu nedenle MobileNet modeli kullanılarak oluşturulan model, derin öğrenme ile meyvelerin tazelik durumlarının tespiti işlemini gerçekleştiren bir mobil uygulama olarak tasarlanmıştır. Veri setinin genişletilmesiyle beraber daha doğru ve geniş çaplı sonuçların elde edilmesi öngörülmektedir. Yapılan çalışma, veri setinin de genişletilmesiyle beraber meyvenin tazelik durumunun tespitini gerektiren çeşitli alanlarda etkin şekilde kullanılabilir hale getirilebilir.

Eğitilen model mobilde Tensorflow Lite teknolojisiyle sınıflandırmaya hazır hale getirilmiştir. Tespit işlemi yapılması sırasında UI Thread' de donmanın engellenmesi adına bu kısım paralel olarak kodlanmıştır. Paralel kodlama AsyncTask yapısı kullanılarak sağlanmıştır.

<p align="center">
  <img src="https://github.com/ayseyk/ayseyk/blob/main/Splash.gif" width="500">
</p>
