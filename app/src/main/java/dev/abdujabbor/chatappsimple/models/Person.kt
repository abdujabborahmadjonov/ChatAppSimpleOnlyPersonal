package dev.abdujabbor.chatappsimple.models
 class MyPerson {
     var uid: String? = null
     var photoUrl: String? = null
     var displayName: String?= null

     constructor(uid: String?, photoUrl: String?, displayName: String?) {
         this.uid = uid
         this.photoUrl = photoUrl
         this.displayName = displayName
     }
     constructor()
 }
