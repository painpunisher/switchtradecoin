package app.start.beta;

import java.io.File;
import java.net.URISyntaxException;

class ProgramDirectoryUtilities
{
    private static String getJarName()
    {
        return new File(ProgramDirectoryUtilities.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }

    private static boolean isRunningFromJAR()
    {
        String jarName = getJarName();
        return jarName.contains(".jar");
    }

    public static String getProgramDirectory()
    {
        if (isRunningFromJAR())
        {
            return getCurrentJARDirectory();
        } else
        {
            return getCurrentProjectDirectory();
        }
    }

    private static String getCurrentProjectDirectory()
    {
        return new File("").getAbsolutePath();
    }

    private static String getCurrentJARDirectory()
    {
        try
        {
            return getCurrentJARFilePath().getParent();
        } catch (URISyntaxException exception)
        {
            exception.printStackTrace();
        }

        throw new IllegalStateException("Unexpected null JAR path");
    }

    static File getCurrentJARFilePath() throws URISyntaxException
    {
        return new File(ProgramDirectoryUtilities.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    }
}
