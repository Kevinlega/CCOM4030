//
//  CameraViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 11/12/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class CameraViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        
    
    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    @IBOutlet weak var imageView: UIImageView!
    
    @IBAction func importGallery(_ sender: Any) {
        let imagePickerController = UIImagePickerController()
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary){
            imagePickerController.delegate = self
            imagePickerController.sourceType = .photoLibrary
            imagePickerController.allowsEditing = false
            self.present(imagePickerController, animated: true, completion: nil )
        }
        else{
            print("photo library not available")
        }
        
    }
    
    @IBAction func openCamera(_ sender: Any) {
        
        let cameraController = UIImagePickerController()
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            cameraController.delegate = self
            cameraController.sourceType = .camera;
            cameraController.allowsEditing = false
            self.present(cameraController, animated: true, completion: nil )
        }
        else {
            print("Camera not available")
        }
    }
    
    
    @IBAction func savePhoto(_ sender: Any) {
        guard let selectedImage = imageView.image else {
            print("Image not found!")
            return
        }
        UIImageWriteToSavedPhotosAlbum(selectedImage, self, nil, nil)
    }
    
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        
        let image = info[UIImagePickerController.InfoKey.originalImage] as! UIImage
        
        imageView.image = image
        
        picker.dismiss(animated: true, completion: nil)
        
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
}
