//
//  NotesViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 12/2/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class NotesViewController: UIViewController, UITextViewDelegate {
    
    
    var user_id = Int()
    var project_id = Int()
    var projectPath = String()
    
    @IBOutlet weak var Note: UITextView!
    @IBOutlet weak var CharacterCount: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        CharacterCount.text = "Characters: 0"
        hideKeyboardWhenTappedAround()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    // MARK: - Alert Function
    public func Alert(title: String, message: String, Dismiss: String) -> UIAlertController{
        let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
        alertController.addAction(UIAlertAction.init(title: Dismiss, style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
        
        return alertController
    }
    
    // MARK: - Connect to API
    public func ConnectToAPI(request: URLRequest) -> NSDictionary{
        
        var json : NSDictionary = NSDictionary()
        let group = DispatchGroup()
        group.enter()
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            do{
                json = try! JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! NSDictionary
                group.leave()
            }
        }
        task.resume()
        group.wait()
        return json
    }
    
    @IBAction func SaveButton(_ sender: Any){
        if Note.text.count > 0 && Note.text.count < 501 {
            //Save
            
            // 1)Tienes que pasarle a este view el project path,
            // 2)No me deja conectar del storyboard a aqui asi que no puedo hacerle
            // input al textfield para que nombres el file como quieras.
            // Cuando hagas esas dos cosas, borras esto:

            let project_path = projectPath + "/docs/"
            //file name received from text field
            // Cuan dificil es conseguir un timestamp? AH APPLE?
            let dateFormatter : DateFormatter = DateFormatter()
            //        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
            dateFormatter.dateFormat = "yyyyMMdd_HHmmss"
            let date = Date()
            let dateString = dateFormatter.string(from: date)
            let fileName = "NOTES_\(user_id)_" + dateString + "_.txt"
            
            // text to save
            let text = String(Note.text)
            // construct the path, path = project path + docs (because its a text file)
            // + file Name, if file name is empty use timestamp.
            let path = project_path + fileName
            
            var response : NSDictionary = NSDictionary()
            // Create the request to the API
            let fileType = "0"
            let url = URL(string: "http://54.81.239.120/fUploadAPI.php")
            var request = URLRequest(url:url!)
            request.httpMethod = "POST"
            let post = "fileType=\(fileType)&text=\(text)&path=\(path)"
            request.httpBody = post.data(using: String.Encoding.utf8)
            
            
            response = ConnectToAPI(request: request)
            let success = response["file_created"] as! Bool
            
            if success{
                self.present(Alert(title: "Uploaded", message: "You may see it from project view.", Dismiss: "Dismiss"),animated: true, completion: nil)
                
                // Devolver al user a el folder de proyecto o desplegar mensaje
            }
            else{
                self.present(Alert(title: "Could not save note.", message: "Please, try again", Dismiss: "Dismiss"),animated: true, completion: nil)
            }
            
        }
        else{
            //Display alert
            self.present(Alert(title: "Could not save note.", message: "Character count should be in range (1,500) ", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
    }
    @IBAction func ClearButton(_ sender: Any){
        Note.text = ""
    }
    
    func textViewDidChange(_ textView: UITextView) {
        let count = "Characters: " + String(textView.text.count)
        CharacterCount.text = count
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        print(2)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            print(user_id)
            print(project_id)
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
}
