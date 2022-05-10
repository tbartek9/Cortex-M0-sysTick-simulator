package appGui;

public class Cortex_M0_SysTick {
	boolean enable; // //bity rejestru statusowego
    boolean countFlag;
    boolean tickint;
    boolean clksrc;
    String clksrcSTR;
       
    boolean enableFlag;
    boolean interruptFlag;
    boolean interrupt; //przerwanie
    String interruptStatus;

    int CVR; 
    int RVR;
    int CSR; 
    
    public Cortex_M0_SysTick()
    {
       reset();//konstruktor resetuje 
       interruptFlag=false;
       interruptStatus="OFF"; //czy jest pozwolenie na przerwanie
       enableFlag=false;
       countFlag=false;
       interrupt=false;    
       CVR=0;
       CSR=0;
       RVR=0;
    }
    
    public void reset()
    {
        enable=false;
        countFlag=false;
        tickint=false;
        clksrc=true;
        clksrcSTR="internal";
    }
    
    public void tickInternal()
    {   
        if(clksrc==true & enable==true) //jesli zrodlo jest ustawione na internal i licznik jest enable
        {
            CVR--;
            if(CVR==0)
                setCountFlag(true); //jesli licznik ustawia sie na zero, flaga countFlag jest ustawiania
            if (CVR<0) {
                CVR=RVR;            //licznik sie przeladowuje, RVR->CVR
                if(tickint=true)    //jesli przerwania sa wlaczone
                    setInterrupt(true); 
            }
            else{
            	setInterrupt(false);
            }//zgloszenie przerwania
        }
    }
    
    public void tickExternal()
    {   
        if(clksrc==false & enable==true)  ///tu wszystkie komentarze analogicznie jak w tick internal
        {
              CVR--; 
            if(CVR==0)
                setCountFlag(true);
            if (CVR<0) {
                CVR=RVR;
                if(tickint=true)
                    setInterrupt(true);
            }
            else {
            	setInterrupt(false);
            }
        }
    }
    
    public void setRVR(int rvr) //trzeba ustawic zeby nie bylo ujemne
    {
        if(rvr>=0 & rvr<(1<<24)) //zeby nie zaladowac wartosci mniejszej niz 0 do licznika;  16777216 to 2^24, licznik jest 24 bit
            RVR=rvr;
            if(rvr==0)
            setDisable();//jeœli ustawiamy RVR na 0, licznik powinien byc disabled
        else if(rvr<0)                     
            RVR=(1<<24)+rvr;        
        else if(rvr>=(1<<24))
            RVR=rvr&((1<<24)-1);             
    }
    
    public void setCVR(int cvr)
    {
        CVR=0;
        setCountFlag(false);
    }
    
    public void setCSR(int csr)
    {
        CSR=csr;
    }
    
    public void setSource(boolean give_source)
    {
        clksrc=give_source;
        if (give_source==true) {clksrcSTR="internal";}else {clksrcSTR="external";}
    }
    
    public void setEnable()
    {
        enable=true;
        enableFlag=true;
    }
    
    public void setDisable()
    {
        enable=false;
        enableFlag=false;
        CVR=0;
    }
    
    public void setInterruptEnable()
    {
        tickint =true;
        interruptFlag=true;
        interruptStatus="ON";
    }
    
    public void setInterruptDisable()
    {
        tickint=false;
        interruptFlag=false;
        interruptStatus="OFF";
    }
    
    public void setInterrupt(boolean set_interrupt)
    {
        interrupt=set_interrupt;
    }
    
    public void setCountFlag(boolean countflag)
    {
        countFlag=countflag;
    }
    
    public int getCVR()
    {
        return CVR;
    } 
    
    public int getRVR()
    {
        return RVR;
    }
    
    public int getCSR()
    {
        setCountFlag(false);
        return CSR;
    }
    
    public boolean getSource()
    {
        setCountFlag(false);
        return clksrc;
    }
    
    public boolean getCountFlag()
    {
        boolean help=countFlag;
        setCountFlag(false);
        return help;
    }
    
    public boolean getEnabled()
    {
        setCountFlag(false);
        return enable;
    }
    
    public boolean getInterrupt()
    {
        setCountFlag(false);
        return tickint;
    }
    public boolean isCountFlag()
    {
        return countFlag;
    }
    
    public boolean isEnableFlag()
    {
        if(enableFlag==true)
            return true;
        else 
            return false;
    }
    
    public boolean isInterruptFlag()
    {
        if(interruptFlag==true)
            return true;
        else 
            return false;
    }
    
    public boolean isInterrupt()
    {
         return interrupt;
    }

}

