package com.homedepot.bigbricks.data

/**
  * Created by Ferosh Jacob on 11/29/16.
  */


import java.io.{File, FileInputStream}
import java.util.Arrays

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.{HttpTransport, InputStreamContent}
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.storage.model.{ObjectAccessControl, StorageObject}
import com.google.api.services.storage.{Storage, StorageScopes}
import com.google.api.client.http.{HttpRequest, HttpRequestInitializer}
trait UploadToCloud {

  class GCSCloudIntializer(credential: GoogleCredential) extends HttpRequestInitializer {
    override def initialize(httpRequest: HttpRequest): Unit = {
      credential.initialize(httpRequest)
      httpRequest.setConnectTimeout(300 * 60000)
      httpRequest.setReadTimeout(300 * 60000)
    }
  }
  private def getStorage: Storage = {
    val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport
    val jsonFactory: JsonFactory = new GsonFactory
    var credential: GoogleCredential = GoogleCredential.getApplicationDefault(httpTransport, jsonFactory)
    if (credential.createScopedRequired) {
      credential = credential.createScoped(StorageScopes.all)
    }
    return new Storage.Builder(httpTransport, new GsonFactory, credential).setHttpRequestInitializer(new GCSCloudIntializer(credential)).setApplicationName("BigBricks").build
  }

  def uploadZipFile(file: File, bucketName: String) {
    val contentStream: InputStreamContent = new InputStreamContent("application/zip", new FileInputStream(file))
    contentStream.setLength(file.length)
    val objectMetadata: StorageObject = new StorageObject().setName(file.getName).setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")))
    val client: Storage = getStorage
    val insertRequest: Storage#Objects#Insert = client.objects.insert(bucketName, objectMetadata, contentStream)
    insertRequest.execute
  }

}
