// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : NotesViewController.swift
// Description : Displays a textbox to user and receives his input to save as text file
// Copyright © 2018 Los Duendes Malvados. All rights reserved.


import UIKit

class NotesViewController: UIViewController, UITextViewDelegate {
    
    // MARK: - Variables
    var user_id = Int()
    var project_id = Int()
    var projectPath = String()
    
    @IBOutlet weak var Note: UITextView!
    @IBOutlet weak var CharacterCount: UILabel!
    
    // When view loads, character count will be 0
    override func viewDidLoad() {
        super.viewDidLoad()
        CharacterCount.text = "Characters: 0"
        hideKeyboardWhenTappedAround()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    // MARK: - Alert Function
    // Displays alert box to user
    public func Alert(title: String, message: String, Dismiss: String) -> UIAlertController{
        let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
        alertController.addAction(UIAlertAction.init(title: Dismiss, style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
        
        return alertController
    }
    
    // MARK: - Connect to API
    // Receives a request, connects to API and returns API response
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
        // Save note if character count is in boundary
        if Note.text.count > 0 {
            
            // Project path in server
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
            self.present(Alert(title: "Could not save note.", message: "Note cannot be empty.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
    }
    @IBAction func ClearButton(_ sender: Any){
        Note.text = ""
        CharacterCount.text = "Characters: 0"
    }
    
    //MARK: - Text Change
    // Keep track of number of characters in text view
    func textViewDidChange(_ textView: UITextView) {
        let count = "Characters: " + String(textView.text.count)
        CharacterCount.text = count
    }
    
    // MARK: - Segue
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
}
