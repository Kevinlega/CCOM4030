//
//  VideoViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 12/3/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit
import MobileCoreServices

import AVKit
import AVFoundation


class VideoViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate{

    
    // MARK: - Variables
    var controller = UIImagePickerController()
    let videoFileName = "/video.mp4"
    
    var selected : URL!
    var saved = false
    
    
    var user_id = Int()
    var project_id = Int()
    var projectPath = String()
    
    @IBAction func importGallery(_ sender: Any) {
        controller.sourceType = UIImagePickerController.SourceType.photoLibrary
        controller.mediaTypes = [kUTTypeMovie as String]
        controller.delegate = self
    
        present(controller, animated: true, completion: nil)
        saved = true
    }
    
    
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
    
    @IBAction func play(_ sender: Any) {
        playVideo()
    }
    
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

    
    
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        // 1
        
        if let _:URL = (info[UIImagePickerController.InfoKey.mediaURL] as? URL) {
            selected = (info[UIImagePickerController.InfoKey.mediaURL] as? URL)
        }
        picker.dismiss(animated: true)
    }
    
    
    @objc func videoSaved(_ video: String, didFinishSavingWithError error: NSError!, context: UnsafeMutableRawPointer){
        if let theError = error {
            print("error saving the video = \(theError)")
        } else {
            DispatchQueue.main.async(execute: { () -> Void in
            })
        }
    }
    
    
    @IBAction func savePhoto(_ sender: Any) {
        
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
    
    
    // Le dimos al boton de upload o save
    @IBAction func Upload(_ sender: Any){
        if (selected != nil){
            myVideoUploadRequest()
        }
    
    }
    
    func myVideoUploadRequest(){
        
        let myUrl = NSURL(string: "http://54.81.239.120/fUploadAPI.php");
        
        let request = NSMutableURLRequest(url:myUrl! as URL);
        request.httpMethod = "POST";
        
        let param = [
            "fileType":"1",
            "path":(projectPath + "/videos/")
        ]
        
        let boundary = generateBoundaryString()
        
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        
        let selectedVideo = selected
        
        let videoData = try? Data(contentsOf: selectedVideo!)
        
        
        if(videoData==nil)  { return; }
        
        request.httpBody = createBodyWithParameters(parameters: param, filePathKey: "file", imageDataKey: videoData! as NSData, boundary: boundary) as Data
        
        
        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            if error != nil {
                print("error=\(String(describing: error))")
                return
            }
            
            // You can print out response object
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
    
    
    func createBodyWithParameters(parameters: [String: String]?, filePathKey: String?, imageDataKey: NSData, boundary: String) -> NSData {
        let body = NSMutableData();
        
        if parameters != nil {
            for (key, value) in parameters! {
                body.appendString(string: "--\(boundary)\r\n")
                body.appendString(string: "Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n")
                body.appendString(string: "\(value)\r\n")
            }
        }
        
        let dateFormatter : DateFormatter = DateFormatter()
        //        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        dateFormatter.dateFormat = "yyyyMMdd_HHmmss"
        let date = Date()
        let dateString = dateFormatter.string(from: date)
        let filename = "VIDEO_\(user_id)_" + dateString + "_.mp4"
        
        let mimetype = "video/mp4"
        
        body.appendString(string: "--\(boundary)\r\n")
        body.appendString(string: "Content-Disposition: form-data; name=\"\(filePathKey!)\"; filename=\"\(filename)\"\r\n")
        body.appendString(string: "Content-Type: \(mimetype)\r\n\r\n")
        body.append(imageDataKey as Data)
        body.appendString(string: "\r\n")
        
        
        
        body.appendString(string: "--\(boundary)--\r\n")
        
        return body
    }
    
    func generateBoundaryString() -> String {
        return "Boundary-\(NSUUID().uuidString)"
    }
}
