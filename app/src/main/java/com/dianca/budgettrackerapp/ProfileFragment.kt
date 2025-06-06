package com.dianca.budgettrackerapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dianca.budgettrackerapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var user: FirebaseUser? = null
    private val IMAGE_PICK_CODE = 1001
    private var imageUri: Uri? = null
    private var hasProfilePicture = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        user = auth.currentUser

        user?.let {
            val email = it.email ?: "Unknown"
            binding.welcomeText.text = "Welcome, ${email.substringBefore("@")}"
            binding.emailDisplay.text = "Email: $email"
            loadProfilePicture()
        } ?: run {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        //remove profile picture
        binding.btnRemoveProfilePicture.setOnClickListener {
            removeProfilePicture()
        }

        //change profile picture
        binding.btnChangeProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.btnSavePassword.setOnClickListener {
            val currentPassword = binding.currentPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()

            if (currentPassword.isNotEmpty() && newPassword.length >= 6) {
                val email = user?.email
                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)
                    user?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                                Toast.makeText(
                                    requireContext(),
                                    if (task.isSuccessful) "Password updated" else "Failed to update password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Current password incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Enter valid credentials (new password ≥ 6 chars)", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    user?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), RegisterActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Account deletion failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    //handles the result from the image picker activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //check if the result is from image picking and was successful
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageUri?.let {
                //method - uploads image to firestore
                uploadProfilePictureToFirestore(it)
            }
        }
    }

    //uploads profile picture to firestore
    private fun uploadProfilePictureToFirestore(uri: Uri) {
        val uid = user?.uid ?: return

        //convert image uri to bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        val outputStream = java.io.ByteArrayOutputStream()
        //compress image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val compressedBytes = outputStream.toByteArray()
        val base64Image = android.util.Base64.encodeToString(compressedBytes, android.util.Base64.DEFAULT)

        val userMap = mapOf("profilePictureBase64" to base64Image)

        //save the image to firestore
        firestore.collection("user_profile_image").document(uid)
            .set(userMap, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile picture saved to Firestore", Toast.LENGTH_SHORT).show()
                //display uploaded image using glide
                Glide.with(this)
                    .load(android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT))
                    .into(binding.profilePicture)
                //reload profile picture
                loadProfilePicture()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to save image: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
        imageUri = null

    }

    //loads profile picture from firestore and displays it
    private fun loadProfilePicture() {
        val uid = user?.uid ?: return
        firestore.collection("user_profile_image").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val base64Image = document.getString("profilePictureBase64")
                    if (!base64Image.isNullOrEmpty()) {
                        val imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
                        //display image using glide
                        Glide.with(this)
                            .load(imageBytes)
                            .into(binding.profilePicture)

                        hasProfilePicture = true
                        binding.btnRemoveProfilePicture.isEnabled = true
                    } else {
                        //show default if no image
                        showDefaultProfile()
                    }
                } else {
                    //show default if document doesn't exist
                    showDefaultProfile()
                }
            }
            .addOnFailureListener {
                //show default if fetch fails
                showDefaultProfile()
            }
    }

    //shows default profile image if user has no uploaded picture
    private fun showDefaultProfile() {
        binding.profilePicture.setImageResource(R.drawable.default_profile)
        hasProfilePicture = false
        binding.btnRemoveProfilePicture.isEnabled = false
    }

    //removes profile picture from firestore
    private fun removeProfilePicture() {
        val uid = user?.uid ?: return

        firestore.collection("user_profile_image").document(uid)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile picture removed", Toast.LENGTH_SHORT).show()
                //reload to show default
                loadProfilePicture()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to remove picture: ${exception.message}", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
    }

}