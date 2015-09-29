package com.example.redfootpos.object;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFMaker {
	
	private File fileDir;
	private static File fileDirStatic;
	private Document document;
	private Paragraph content;
	private static String localDir = "POS_Pdf";
	//
	
	public static final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	public static final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	public static final Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.BLUE);
	public static final Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	public static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	
	public enum fonttype{ 
		catFont,
		redFont,
		subFont,
		smallBold,
		normal
	}

	public PDFMaker(String filename){
		//
		fileDir = new File(Environment.getExternalStorageDirectory(), localDir);
		
		if(!fileDir.exists()) fileDir.mkdirs();
		
		String filePath = fileDir.getAbsolutePath() + "/" + filename+".pdf";
		
		fileDirStatic = new File(filePath);
		
		try {
			document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(filePath));
			document.open();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static String GetFilePath(){
		return fileDirStatic.getAbsolutePath();
	}
	
	public static void OpenPDF(Context core, String filename){
		
		filename = filename+".pdf";
		
		File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+localDir+"/"+filename);
		Intent target = new Intent(Intent.ACTION_VIEW);
		
		target.setDataAndType(Uri.fromFile(file), "application/pdf");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		
		Intent intent = Intent.createChooser(target, "Open a file");
		core.startActivity(intent);
	}
	
	public void AddMetaData(String title, String subject, String author){
		document.addTitle(title);
		document.addSubject(subject);
		document.addAuthor(author);
	}
	
	public void InitParagraph(){
		content = new Paragraph();
	}
	
	public void AddContent(String message, fonttype fonttype){
		
		switch(fonttype){
		case normal:
			content.add(new Paragraph(message));
		case catFont:
			content.add(new Paragraph(message, catFont));
			break;
		case smallBold:
			content.add(new Paragraph(message, smallBold));
			break;
		case redFont:
			content.add(new Paragraph(message, redFont));
			break;
		default:
			break;
		}
		
	}
	
	public void newLine(int rows){
		 for(int i = 0; i < rows; i++){
			 content.add(new Paragraph(" "));
		 }	
	}
	
	public void addEmptyLine(Paragraph paragraph, int number) {
	    for (int i = 0; i < number; i++) {
	      paragraph.add(new Paragraph(" "));
	    }
	  }
	
	public void post(){
		try {
			document.add(content);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void post(Paragraph prag){
		
		try {
			document.add(prag);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void post(Chapter chap){
		
		try {
			document.add(chap);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void newPage(){
		document.newPage();
	}
	
	public void close(){
		document.close();
	}
}
