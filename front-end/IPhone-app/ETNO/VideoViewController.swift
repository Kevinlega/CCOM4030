// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : VideoViewController.swift
// Description : View controller that allows users to upload and download videos
//               to/from the image view
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit
import MobileCoreServices

import AVKit
import AVFoundation


class VideoViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate{

    
    // MARK: - Variables
    // Select videos
    var controller = UIImagePickerController()
    // Name video
    let videoFileName = "/video.mp4"
    
    
    // Global variables for file upload or user validation
    var selected : URL!
    var saved = false
    var user_id = Int()
    var project_id = Int()
    var projectPath = String()
    
    // Import video from gallery
    @IBAction func importGallery(_ sender: Any) {
        controller.sourceType = UIImagePickerController.SourceType.photoLibrary
        controller.mediaTypes = [kUTTypeMovie as String]
        controller.delegate = self
    
        present(controller, animated: true, completion: nil)
        saved = true
    }
    
    // When button is pressed access users' camera
    @IBAction func openVideo(_ sender: Any) {
    
        saved = false
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            
            controller.sourceType = .camera
            controller.mediaTypes = [kUTTypeMovie as String]
            controller.delegate = self
            
            present(controller, animated: true, completion: nil)
        }
        else {
            print("Camera is not available")
        }
        
    }
    
    // When button is pressed play video
    @IBAction func play(_ sender: Any) {
        playVideo()
    }
    
    // Play video using AVPlayer
    private func playVideo() {
        if ((selected) != nil){
            let player = AVPlayer(url: selected)
            let playerController = AVPlayerViewController()
            playerController.player = player
            present(playerController, animated: true) {
            player.play()
            }
        }
    }

    // Select video for import
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        
        if let _:URL = (info[UIImagePickerController.InfoKey.mediaURL] as? URL) {
            selected = (info[UIImagePickerController.InfoKey.mediaURL] as? URL)
        }
        picker.dismiss(animated: true)
    }
    
    // Was the video saved to device?
    @objc func videoSaved(_ video: String, didFinishSavingWithError error: NSError!, context: UnsafeMutableRawPointer){
        if let theError = error {
            print("error saving the video = \(theError)")
        } else {
            DispatchQueue.main.async(execute: { () -> Void in
            })
        }
    }
    
    // Save video to device
    @IBAction func saveVideo(_ sender: Any) {
        
        if ((selected) != nil && !saved){
            let selectedVideo = selected
            // Save video to the main photo album
            let selectorToCall = #selector(VideoViewController.videoSaved(_:didFinishSavingWithError:context:))
            
            UISaveVideoAtPathToSavedPhotosAlbum(selectedVideo!.relativePath, self, selectorToCall, nil)
            // Save the video to the app directory
            let videoData = try? Data(contentsOf: selectedVideo!)
            let paths = NSSearchPathForDirectoriesInDomains(
                FileManager.SearchPathDirectory.documentDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)
            let documentsDirectory: URL = URL(fileURLWithPath: paths[0])
            let dataPath = documentsDirectory.appendingPathComponent(videoFileName)
            try! videoData?.write(to: dataPath, options: [])
        }
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
    
    // Pass user info to next segue
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    if (segue.identifier == "BackToProject"){
    let vc = segue.destination as! ProjectViewController
    vc.user_id = user_id
    vc.project_id = project_id
        }
    }
    
    
    // When upload button is pressed, upload the video to server
    @IBAction func Upload(_ sender: Any){
        if (selected != nil){
            myVideoUploadRequest()
        }
    
    }
    
    func myVideoUploadRequest(){

        // API URL
        let myUrl = NSURL(string: "http://54.81.239.120/fUploadAPI.php");
        let request = NSMutableURLRequest(url:myUrl! as URL);
       
        // Request method
        request.httpMethod = "POST";
        
        // Dictionary containing parameters for server API
        let param = [
            "fileType":"1",
            "path":(projectPath + "/videos/")
        ]
        
        // Generate a UUID for request header
        let boundary = generateBoundaryString()
        // Request header
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        // Video to be uploaded
        let selectedVideo = selected
        // Convert video to binary
        let videoData = try? Data(contentsOf: selectedVideo!)
        
        // If conversion of video not successful
        if(videoData == nil){return;}
        
        // Generates body for request using a dictionary as parameters for server post request
        request.httpBody = createBodyWithParameters(parameters: param, filePathKey: "file", imageDataKey: videoData! as NSData, boundary: boundary) as Data
        
        // Initiate request task, should receive a json response from server
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error=\(String(describing: error))")
                return
            }
            
            // Print out response object
            print("response = \(String(describing: response))")
            
            // Print out reponse body
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("response data = \(responseString!)")
            
            // Receive response from server as json
            do{
                // Tell user if upload was successfull
                let json = try JSONSerialization.jsonObject(with: data!, options: []) as? NSDictionary
                
                if json!["file_created"] as! Bool == true{
                    self.present(Alert(title: "Uploaded", message: "You may see it from project view.", Dismiss: "Dismiss"),animated: true, completion: nil)
                } else {
                    self.present(Alert(title: "Try Again", message: "Error uploading.", Dismiss: "Dismiss"),animated: true, completion: nil)
                }
            } catch {
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
        
        // If parameters are passed append each one to the request body
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
        // Naming the file
        let filename = "VIDEO_\(user_id)_" + dateString + "_.mp4"
        // File type
        let mimetype = "video/mp4"
        
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
    func generateBoundaryString() -> String{
        return "Boundary-\(NSUUID().uuidString)"
    }
}
