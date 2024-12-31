package com.project.samay.domain.repository

import com.google.android.gms.common.api.Response
import com.google.firebase.firestore.FirebaseFirestore
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.domain.model.ResponseError
import com.project.samay.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BackUpRepository() {
    private val db = FirebaseFirestore.getInstance()

    suspend fun uploadDomains(domains: List<DomainEntity>): Resource<Boolean>{
        return withContext(Dispatchers.IO){
            try{
                for(domain in domains){
                    db
                        .collection("samay")
                        .document("domains")
                        .collection("domains")
                        .document(domain.id.toString())
                        .set(domain)
                        .await()
                }
                Resource.success(true)
            }catch (e: Exception){
                val error= ResponseError.UNKNOWN
                error.actualResponse = e.message
                Resource.failure(error = ResponseError.UNKNOWN)
            }
        }
    }

    suspend fun fetchDomains(): Resource<List<DomainEntity>>{
        return withContext(Dispatchers.IO){
            try{
                val querySnapshot = db
                    .collection("samay")
                    .document("domains")
                    .collection("domains")
                    .get()
                    .await()
                val domains = querySnapshot.toObjects(DomainEntity::class.java)
                Resource.success(domains)
            }catch (e: Exception){
                val error = ResponseError.UNKNOWN
                error.actualResponse = e.message
                Resource.failure(error = ResponseError.UNKNOWN)
            }
        }
    }

    suspend fun deleteAllDomainsFromFirebase(): Resource<Boolean>{
        return withContext(Dispatchers.IO){
            try{
                db
                    .collection("samay")
                    .document("domains")
                    .collection("domains")
                    .get()
                    .await()
                    .documents
                    .forEach {
                        it.reference.delete().await()
                    }
                Resource.success(true)
            }catch (e: Exception){
                val error = ResponseError.UNKNOWN
                error.actualResponse = e.message
                Resource.failure(error = ResponseError.UNKNOWN)
            }
        }
    }

    suspend fun uploadHistory(history: List<HistoryEntity>): Resource<Boolean>{
        return withContext(Dispatchers.IO){
            try{
                for(h in history){
                    db
                        .collection("samay")
                        .document("history")
                        .collection("history")
                        .document(h.hId.toString())
                        .set(h)
                        .await()
                }
                Resource.success(true)
            }catch (e: Exception){
                val error = ResponseError.UNKNOWN
                error.actualResponse = e.message
                Resource.failure(error = ResponseError.UNKNOWN)
            }
        }
    }

    suspend fun fetchHistory(): Resource<List<HistoryEntity>>{
        return withContext(Dispatchers.IO){
            try{
                val querySnapshot = db
                    .collection("samay")
                    .document("history")
                    .collection("history")
                    .get()
                    .await()
                val history = querySnapshot.toObjects(HistoryEntity::class.java)
                Resource.success(history)
            }catch (e: Exception){
                val error = ResponseError.UNKNOWN
                error.actualResponse = e.message
                Resource.failure(error = ResponseError.UNKNOWN)
            }
        }
    }

    suspend fun deleteAllHistoryFromFirebase(): Resource<Boolean>{
        return withContext(Dispatchers.IO){
            try{
                db
                    .collection("samay")
                    .document("history")
                    .collection("history")
                    .get()
                    .await()
                    .documents
                    .forEach {
                        it.reference.delete().await()
                    }
                Resource.success(true)
            }catch (e: Exception){
                val error = ResponseError.UNKNOWN
                error.actualResponse = e.message
                Resource.failure(error = ResponseError.UNKNOWN)
            }
        }
    }


}