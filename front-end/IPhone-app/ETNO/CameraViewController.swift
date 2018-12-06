// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : CameraViewController.swift
// Description : View controller that allows upload of an image to an image view
//               via camera or via gallery.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit

class CameraViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        
    
    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var projectPath = String()
    var saved = false

    // Image portrait
    @IBOutlet weak var imageView: UIImageView!
    
    // Import image from gallery
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
        saved = true
        
    }
    
    // When pressed accesses users camera
    @IBAction func openCamera(_ sender: Any) {
        
        let cameraController = UIImagePickerController()
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            cameraController.delegate = self
            cameraController.sourceType = .camera;
            cameraController.allowsEditing = false
            self.present(cameraController, animated: true, completion: nil )
            saved = false
        }
        else {
            print("Camera not available")
        }
    }
    
    // Save image in view to device
    @IBAction func savePhoto(_ sender: Any) {
        
        if (!saved){
            guard let selectedImage = imageView.image else {
                print("Image not found!")
                return
            }
            UIImageWriteToSavedPhotosAlbum(selectedImage, self, nil, nil)
            saved = true
            self.present(Alert(title: "Saved", message: "You may see the image in gallery.", Dismiss: "Dismiss"),animated: true, completion: nil)
        } else{
            self.present(Alert(title: "Nothing to Save", message: "Image already in gallery.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
    }
    
    // Select image for import
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        
        let image = info[UIImagePickerController.InfoKey.originalImage] as! UIImage
        imageView.image = image
        picker.dismiss(animated: true, completion: nil)
        
    }
    
    // Cancel image import
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: nil)
    }
    
    // Default
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    // Default
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // Pass values to segue for user validation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
    

    // When upload button is pressed, take image currently in the view
    // and upload it to server.
    @IBAction func Upload(_ sender: Any){
        myImageUploadRequest()
    }
    
    func myImageUploadRequest(){
        
        // API URL
        let myUrl = NSURL(string: "http://54.81.239.120/fUploadAPI.php");
        let request = NSMutableURLRequest(url:myUrl! as URL);
        
        // Request method
        request.httpMethod = "POST";
        
        // Dictionary containing parameters for server API
        let param = ["fileType":"1", "path":(projectPath + "/images/")]
        
        // Header for request
        let boundary = generateBoundaryString()
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        // Receive image from view as a compressed jpeg,
        // note that compression rate is 0.0 so no compression is ocurring.
        let imageData = imageView.image?.jpegData(compressionQuality: 0.0)
        
        // If image compression was successful continue
        if(imageData==nil)  { return; }
        
        // Generates body for request using a dictionary as parameters for server post request
        request.httpBody = createBodyWithParameters(parameters: param, filePathKey: "file", imageDataKey: imageData! as NSData, boundary: boundary) as Data
        
        // Initiate request task, should receive a json response from server
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            if error != nil {
                print("error=\(String(describing: error))")
                return
            }
            
            // Print out response object
            print("******* response = \(String(describing: response))")
            
            // Print out reponse body
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("****** response data = \(responseString!)")
            
            do {
                let json = try JSONSerialization.jsonObject(with: data!, options: []) as? NSDictionary
                
                if json!["file_created"] as! Bool == true{
                    self.present(Alert(title: "Uploaded", message: "You may see it from project view.", Dismiss: "Dismiss"),animated: true, completion: nil)
                } else{
                    self.present(Alert(title: "Try Again", message: "Error uploading.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
                
                
            }catch
            {
                print(error)
            }
            
        }
        
        task.resume()
    }
    
    
    // Receives an array of strings simulating a json to create the body for upload request.
    // This body contains a series of hashes and the pertinent info for file upload.
    // Note: \r\n refers to end of line

    func createBodyWithParameters(parameters: [String: String]?, filePathKey: String?, imageDataKey: NSData, boundary: String) -> NSData {
        let body = NSMutableData();
        
        if parameters != nil {
            for (key, value) in parameters! {
                body.appendString(string: "--\(boundary)\r\n")
                body.appendString(string: "Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n")
                body.appendString(string: "\(value)\r\n")
            }
        }
        
        // Creating a format for date to timestamp files
        let dateFormatter : DateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMdd_HHmmss"
        let date = Date()
        let dateString = dateFormatter.string(from: date)
       
        // File Name
        let filename = "IMAGE_\(user_id)_" + dateString + "_.jpg"
        // File type
        let mimetype = "image/jpg"
        
        // Some other info, here binary data from file is appended to request body
        body.appendString(string: "--\(boundary)\r\n")
        body.appendString(string: "Content-Disposition: form-data; name=\"\(filePathKey!)\"; filename=\"\(filename)\"\r\n")
        body.appendString(string: "Content-Type: \(mimetype)\r\n\r\n")
        body.append(imageDataKey as Data)
        body.appendString(string: "\r\n")
        body.appendString(string: "--\(boundary)--\r\n")
        
        return body
    }

    // Generates a UUID for request boundary.
    func generateBoundaryString() -> String {
        return "Boundary-\(NSUUID().uuidString)"
    }
}

// Converts string to data
extension NSMutableData {
    func appendString(string: String) {
        let data = string.data(using: String.Encoding.utf8, allowLossyConversion: true)
        append(data!)
    }
}
