//
//  ProjectViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/4/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit

class ProjectViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
    
    
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

    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        
        let image = info[UIImagePickerController.InfoKey.originalImage] as! UIImage
        
        imageView.image = image
        
        picker.dismiss(animated: true, completion: nil)
        
    }

    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }

    
    
    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var is_admin = Bool()
    @IBOutlet weak var AddParticipant: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        is_admin = CheckAdmin(project_id: project_id, user_id: user_id)
        if !is_admin{
            AddParticipant.isHidden = true
        }
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "AddParticipants"){
            let vc = segue.destination as! AddParticipantViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
        else if (segue.identifier == "BackToDashboard"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        }
    }
}
