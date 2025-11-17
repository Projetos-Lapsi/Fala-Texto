import fitz 
doc = fitz.open("Laudo4.pdf")           
page = doc[0]                         # choose some page
rect = fitz.Rect(50, 100, 200, 200)   # rectangle (left, top, right, bottom) in pixels

text = "absolutely not"

rc = page.insertTextbox(rect, text, fontsize = 48, # choose fontsize (float)
                   fontname = "Times-Roman",       # a PDF standard font
                   fontfile = None,                # could be a file on your system
                   align = 1)                      # 0 = left, 1 = center, 2 = right

#doc.saveIncr()   # update file. Save to new instead by doc.save("new.pdf",...)
doc.save("/Users/khaylablack/Desktop/watermarked_participant_cert.pdf")
