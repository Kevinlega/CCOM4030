//
//  AddParticipantViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class AddParticipantViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate{
   
    var users = [String()]
    var FilteredUsers = [String()]
    var SelectedUsers = [String()]
    
    var FirstSelected = true
    var Searching = false
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!

    // Create connection
    
    func GetUsers(){
        let apiLink = URL(string: "http://54.81.239.120/API.php?query=1")
        
        let task = URLSession.shared.dataTask(with: apiLink!, completionHandler: {(data, response, error) -> Void in
            do
            {
             
                self.users = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String]
                
                DispatchQueue.main.async {
                    
                    self.tableView.reloadData()
                
                }
                
            }
            catch{
                // nothing

            }
            })
        task.resume()
        
    }

    // Work with the table
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if Searching{
            return FilteredUsers.count
        }
        return SelectedUsers.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
        if Searching{
            cell.textLabel?.text = FilteredUsers[indexPath.row]
        }
        else{
            cell.textLabel?.text = SelectedUsers[indexPath.row]
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if !Searching{
            if let selectedUser = tableView.cellForRow(at: indexPath){
                let indexToDelete = SelectedUsers.firstIndex(of: (selectedUser.textLabel?.text)!)
                SelectedUsers.remove(at: indexToDelete!)
                DispatchQueue.main.asyncAfter(deadline: (DispatchTime.now()+0.25), execute: {tableView.reloadData()})
                
            }
            }
            
        else{
            
            if let selectedUser = tableView.cellForRow(at: indexPath){
                if !(SelectedUsers.contains((selectedUser.textLabel?.text)!)){
                    
                    
                    SelectedUsers.append((selectedUser.textLabel?.text)!)
                
                    if FirstSelected {
                        SelectedUsers.remove(at: 0)
                        FirstSelected = false
                        }
                
                    FilteredUsers = Array(Set(FilteredUsers).subtracting(SelectedUsers))
                    DispatchQueue.main.asyncAfter(deadline: (DispatchTime.now()+0.25), execute: {tableView.reloadData()})
                    
                    }
                
                }
            }
        }
    
    // Work with the search bar
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchBar.text == nil || searchBar.text == ""{
            Searching = false
            view.endEditing(true)
        }
        else{
            if (searchBar.text!.contains("@") && searchBar.text!.count > 5){
                Searching = true
                FilteredUsers = users.filter({$0.localizedCaseInsensitiveContains(searchBar.text!)})
                FilteredUsers = Array(Set(FilteredUsers).subtracting(SelectedUsers))
                
                
            }
            else{
                Searching = false
                FilteredUsers = []
            }
        
        }
        tableView.reloadData()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        GetUsers()
        
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "AddParticipants"){
            let _ = segue.destination as! ProjectViewController
        }
        else if (segue.identifier == "BackToProject"){
            let _ = segue.destination as! ProjectViewController
        }
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
