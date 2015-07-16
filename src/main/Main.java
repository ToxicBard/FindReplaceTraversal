package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import commonTools.FileTools;

public class Main {
	
	private static enum ReplaceTypes { File, Folder, Both };
	
	private static BufferedWriter mOutputWriter;
	private static final ReplaceTypes mReplacementType = ReplaceTypes.Both;
	private static final boolean mWriteChanges = false;
	private static final String mFindToken = " ";
	private static final String mReplaceToken = "_";
	

	public static void main(String[] args) throws IOException {
		mOutputWriter = FileTools.openWriteFile("out/results.txt");
		File checkDirectory = FileTools.selectSavedDirectory("Select the check directory", "cfg/checkDirectory.cfg");
		
		traverseFolder(checkDirectory, mReplacementType, mFindToken, mReplaceToken, mWriteChanges, true);
		
		mOutputWriter.close();
	}
	
	private static void traverseFolder(File lDirectory, ReplaceTypes lReplacementType, String lFindToken, String lReplaceToken, boolean lWriteChanges, boolean lIsRootDirectory) throws IOException{
		File[] listDirectoryFiles = lDirectory.listFiles();
		
		//Sort the files alphabetically
		Arrays.sort(listDirectoryFiles);
		
		
		//Loop through the files in the directory
		for(File loopFile : listDirectoryFiles){
			/*
			 * We only want to compare further if the file/directory actually 
			 * contains the token that we're looking for and it's not the root directory.
			 */
			if(loopFile.getName().contains(mFindToken)){
				//Check to see whether the file is a file or a folder, depending on the replacement type
				switch(lReplacementType){
					case File:
						if(loopFile.isFile()){
							loopFile = RenameFile(loopFile, lFindToken, lReplaceToken, lWriteChanges);
						}
						break;
					case Folder:
						if(loopFile.isDirectory()){
							loopFile = RenameFile(loopFile, lFindToken, lReplaceToken, lWriteChanges);
						}
						break;
					case Both:
						loopFile = RenameFile(loopFile, lFindToken, lReplaceToken, lWriteChanges);
						break;
				}
			}
			
			/*
			 * If the file is a directory and we can pull its child folders, then recursively traverse all child folders
			 */
			
			if(loopFile.isDirectory() && loopFile.listFiles() != null){
				traverseFolder(loopFile, lReplacementType, lFindToken, lReplaceToken, lWriteChanges, false);
			}
		}
	}
	
	private static File RenameFile(File lRenameFile, String lFindToken, String lReplaceToken, boolean lWriteChanges) throws IOException{
		String newName = lRenameFile.getName().replace(lFindToken, lReplaceToken);
		String output = lRenameFile.getName() + ", " + newName;
		File newFile = new File(lRenameFile.getParent(), newName);
		
		System.out.println(output);
		mOutputWriter.write(output + "\n");
		
		if(lWriteChanges){
			if(lRenameFile.renameTo(newFile)){
				//If the file was successfully renamed, then return it so that we can traverse through
				//its files
				return newFile;
			}
			else {
				//I'm considering this an error condition
				System.out.println("Rename failed");
				mOutputWriter.write("Rename failed" + "\n");
				return null;
			}
		}
		
		//If not in WriteChanges mode, then return the file that was passed in, because nothing changed
		return lRenameFile;
		
	}

}
