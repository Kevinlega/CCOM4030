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
    
    @IBAction func importImage(_ sender: Any) {
    
        
        let imagePickerController = UIImagePickerController()
        imagePickerController.delegate = self
        
        
        let actionSheet = UIAlertController(title: "Photo Source", message: "Choose a source", preferredStyle: .actionSheet)
        
        actionSheet.addAction(UIAlertAction(title: "Camera", style: .default, handler: { (action:UIAlertAction) in
        
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
        
        imagePickerController.sourceType = .camera
        self.present(imagePickerController, animated: true, completion: nil )
        }
        else {
        print("Camera not available")
        }
        }))
        
        actionSheet.addAction(UIAlertAction(title: "Photo Library", style: .default, handler: { (action:UIAlertAction) in
        imagePickerController.sourceType = .photoLibrary
        self.present(imagePickerController, animated: true, completion: nil )
        }))
        
        actionSheet.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        
        self.present(actionSheet,animated: true, completion: nil)
        
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
