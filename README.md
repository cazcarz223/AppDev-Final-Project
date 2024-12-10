# Backend-Final-Project
**App name**: Eventer  
**App tagline**: Eventer: Where Connections Happen, One Event at a Time.   
**Important Links:** All implementations are included in this repo (backend is in backend/src and frontend is EventApp). The deployment for the backend is at 34.85.149.0 and the API Specs are at https://docs.google.com/document/d/1ZjasLMu237YTIlTiKBaqpKnpbVa3oFNMi7nMKYAsn9M/edit?usp=sharing   

**Description of App:** 
Eventer is an app where users can discover events that are happening, view details about who created them, see attendee lists, and find information about their location and time. Users can also create their own events by providing a name, location, and date; or join events by adding themselves to the attendee lists

Sample backend code:  
<img width="1126" alt="Screenshot 2024-12-10 at 5 16 10 PM" src="https://github.com/user-attachments/assets/3069fe73-5dc9-488a-a35d-ce02bc874282">
<img width="485" alt="Screenshot 2024-12-10 at 1 09 52 AM" src="https://github.com/user-attachments/assets/9ca69fda-40a3-4ba2-8057-e0378eab66dd">
<img width="546" alt="Screenshot 2024-12-10 at 1 11 32 AM" src="https://github.com/user-attachments/assets/365a52a4-a5af-4e88-a919-00dc0b3bc615">
<img width="633" alt="Screenshot 2024-12-10 at 1 11 51 AM" src="https://github.com/user-attachments/assets/798189c7-6638-4aba-879e-cb71e29bad79">
<img width="411" alt="Screenshot 2024-12-10 at 1 13 59 AM" src="https://github.com/user-attachments/assets/95ee65a6-7b67-476f-ab12-2e4742525ccb">

_ 
## How our app addresses each of the requirements

**Backend:**
- We have two tables in database: Users and Events. They have a many-many relationship between them. 
- We have a total of 8 routes, 1 is DELETE, 3 are POST, 3 are GETs  

The API specs are also at https://docs.google.com/document/d/1ZjasLMu237YTIlTiKBaqpKnpbVa3oFNMi7nMKYAsn9M/edit?usp=sharing 

**Frontend:**
Core app functionalities for Eventer completed, wirh MVVM design pattern implemented and 4 fully funcitonal screens. See section below with regards to integratoin. 

**Anything else you want your grader to know:**
  We had limited time due to late group merging and also one of the two frontend members dropping out (both of which was not known until after Thanksgiving Break). We received an extension and a drop for the midpoint submission. Challenges arose during the integration of the frontend with the backend. Despite our efforts to resolve these issues, including consulting with TAs, the problem could not be resolved within the limited time available,
