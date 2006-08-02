/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.xhtml.Element;
import org.eclipse.swt.internal.xhtml.document;

/**
 * Instances of the receiver represent is an unselectable
 * user interface object that is used to display progress,
 * typically in the form of a bar.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SMOOTH, HORIZONTAL, VERTICAL, INDETERMINATE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */
public class ProgressBar extends Control {
	static final int DELAY = 100;
	static final int TIMER_ID = 100;
	static final int MINIMUM_WIDTH = 100;
	/*
	static final int ProgressBarProc;
	static final TCHAR ProgressBarClass = new TCHAR (0, OS.PROGRESS_CLASS, true);
	static {
		WNDCLASS lpWndClass = new WNDCLASS ();
		OS.GetClassInfo (0, ProgressBarClass, lpWndClass);
		ProgressBarProc = lpWndClass.lpfnWndProc;
		/*
		* Feature in Windows.  The progress bar window class
		* does not include CS_DBLCLKS.  This mean that these
		* controls will not get double click messages such as
		* WM_LBUTTONDBLCLK.  The fix is to register a new 
		* window class with CS_DBLCLKS.
		* 
		* NOTE:  Screen readers look for the exact class name
		* of the control in order to provide the correct kind
		* of assistance.  Therefore, it is critical that the
		* new window class have the same name.  It is possible
		* to register a local window class with the same name
		* as a global class.  Since bits that affect the class
		* are being changed, it is possible that other native
		* code, other than SWT, could create a control with
		* this class name, and fail unexpectedly.
		*-/
		int hInstance = OS.GetModuleHandle (null);
		int hHeap = OS.GetProcessHeap ();
		lpWndClass.hInstance = hInstance;
		lpWndClass.style &= ~OS.CS_GLOBALCLASS;
		lpWndClass.style |= OS.CS_DBLCLKS;
		int byteCount = ProgressBarClass.length () * TCHAR.sizeof;
		int lpszClassName = OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, byteCount);
		OS.MoveMemory (lpszClassName, ProgressBarClass, byteCount);
		lpWndClass.lpszClassName = lpszClassName;
		OS.RegisterClass (lpWndClass);
//		OS.HeapFree (hHeap, 0, lpszClassName);	
	}
	*/
	private int minimum;
	private int maximum;
	private int selection;
	private Element innerHandle;

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together 
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a composite control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#SMOOTH
 * @see SWT#HORIZONTAL
 * @see SWT#VERTICAL
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 * 
 * @j2sIgnore
 */	
public ProgressBar (Composite parent, int style) {
	super (parent, checkStyle (style));
	/*
	minimum = 0;
	maximum = 100;
	*/
}

/*
int callWindowProc (int hwnd, int msg, int wParam, int lParam) {
	if (handle == 0) return 0;
	return OS.CallWindowProc (ProgressBarProc, hwnd, msg, wParam, lParam);
}
*/

static int checkStyle (int style) {
	style |= SWT.NO_FOCUS;
	return checkBits (style, SWT.HORIZONTAL, SWT.VERTICAL, 0, 0, 0, 0);
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int border = getBorderWidth ();
	int width = border * 2, height = border * 2;
	if ((style & SWT.HORIZONTAL) != 0) {
//		width += OS.GetSystemMetrics (OS.SM_CXHSCROLL) * 10;
//		height += OS.GetSystemMetrics (OS.SM_CYHSCROLL);
		/*
		width += UIStringUtil.calculatePlainStringLineWidth("W") * 10;
		height += UIStringUtil.calculatePlainStringLineHeight("W");
		*/
		width += 16 * 10;
		height += 16;
	} else {
//		width += OS.GetSystemMetrics (OS.SM_CXVSCROLL);
//		height += OS.GetSystemMetrics (OS.SM_CYVSCROLL) * 10;
		/*
		width += UIStringUtil.calculatePlainStringLineWidth("W");
		height += UIStringUtil.calculatePlainStringLineHeight("W") * 10;
		*/
		width += 16;
		height += 16 * 10;
	}
	if (wHint != SWT.DEFAULT) width = wHint + (border * 2);
	if (hHint != SWT.DEFAULT) height = hHint + (border * 2);
	return new Point (width, height);
}

void createHandle () {
	//super.createHandle ();
	minimum = 0;
	maximum = 100;
	handle = document.createElement ("DIV");
	handle.className = "progress-bar-default";
	if (parent != null && parent.handle != null) {
		parent.handle.appendChild(handle);
	}
	
	innerHandle = document.createElement("DIV");
	handle.appendChild(innerHandle);
	if ((style & SWT.HORIZONTAL) != 0) {
		innerHandle.className = "progress-bar-horizontal";
	} else {
		innerHandle.className = "progress-bar-vertical";
	}
	startTimer ();
}

//int defaultForeground () {
//	return OS.GetSysColor (OS.COLOR_HIGHLIGHT);
//}

/**
 * Returns the maximum value which the receiver will allow.
 *
 * @return the maximum
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getMaximum () {
	checkWidget ();
	//return OS.SendMessage (handle, OS.PBM_GETRANGE, 0, 0);
	return maximum;
}

/**
 * Returns the minimum value which the receiver will allow.
 *
 * @return the minimum
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getMinimum () {
	checkWidget ();
//	return OS.SendMessage (handle, OS.PBM_GETRANGE, 1, 0);
	return minimum;
}

/**
 * Returns the single 'selection' that is the receiver's position.
 *
 * @return the selection
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getSelection () {
	checkWidget ();
//	return OS.SendMessage (handle, OS.PBM_GETPOS, 0, 0);
	return selection;
}

void releaseWidget () {
	super.releaseWidget ();
	stopTimer ();
}

void startTimer () {
	if ((style & SWT.INDETERMINATE) != 0) {
		/*
		int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
		if (OS.COMCTL32_MAJOR < 6 || (bits & OS.PBS_MARQUEE) == 0) {
			OS.SetTimer (handle, TIMER_ID, DELAY, 0);
		} else {
			OS.SendMessage (handle, OS.PBM_SETMARQUEE, 1, DELAY);
		}
		*/
	}
}

void stopTimer () {
	if ((style & SWT.INDETERMINATE) != 0) {
		/*
		int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
		if (OS.COMCTL32_MAJOR < 6 || (bits & OS.PBS_MARQUEE) == 0) {
			OS.KillTimer (handle, TIMER_ID);
		} else {
			OS.SendMessage (handle, OS.PBM_SETMARQUEE, 0, 0);
		}
		*/
	}
}

/*
void setBackgroundPixel (int pixel) {
	if (background == pixel) return;
	background = pixel;
	if (pixel == -1) pixel = OS.CLR_DEFAULT;
	OS.SendMessage (handle, OS.PBM_SETBKCOLOR, 0, pixel);
}

void setForegroundPixel (int pixel) {
	if (foreground == pixel) return;
	foreground = pixel;
	if (pixel == -1) pixel = OS.CLR_DEFAULT;
	OS.SendMessage (handle, OS.PBM_SETBARCOLOR, 0, pixel);
}
*/

/**
 * Sets the maximum value that the receiver will allow.  This new
 * value will be ignored if it is not greater than the receiver's current
 * minimum value.  If the new maximum is applied then the receiver's
 * selection value will be adjusted if necessary to fall within its new range.
 *
 * @param value the new maximum, which must be greater than the current minimum
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setMaximum (int value) {
	/*
	checkWidget ();
	int minimum = OS.SendMessage (handle, OS.PBM_GETRANGE, 1, 0);
	if (0 <= minimum && minimum < value) {
		OS.SendMessage (handle, OS.PBM_SETRANGE32, minimum, value);
	}
	*/
	if (0 <= minimum && minimum < value) {
		maximum = value;
	}
}

/**
 * Sets the minimum value that the receiver will allow.  This new
 * value will be ignored if it is negative or is not less than the receiver's
 * current maximum value.  If the new minimum is applied then the receiver's
 * selection value will be adjusted if necessary to fall within its new range.
 *
 * @param value the new minimum, which must be nonnegative and less than the current maximum
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setMinimum (int value) {
	checkWidget ();
	/*
	int maximum = OS.SendMessage (handle, OS.PBM_GETRANGE, 0, 0);
	if (0 <= value && value < maximum) {
		OS.SendMessage (handle, OS.PBM_SETRANGE32, value, maximum);
	}
	*/
	if (0 <= value && value < maximum) {
		minimum = value;
	}
}

/**
 * Sets the single 'selection' that is the receiver's
 * position to the argument which must be greater than or equal
 * to zero.
 *
 * @param value the new selection (must be zero or greater)
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (int value) {
	checkWidget ();
//	OS.SendMessage (handle, OS.PBM_SETPOS, value, 0);
	selection = value;
	if ((style & SWT.HORIZONTAL) != 0) {
		innerHandle.style.width = Math.round(getSize().x * selection / maximum) + "px";
	} else {
		innerHandle.style.height = Math.round(getSize().y * selection / maximum) + "px";
	}
}

/*
int widgetStyle () {
	int bits = super.widgetStyle ();
	if ((style & SWT.SMOOTH) != 0) bits |= OS.PBS_SMOOTH;
	if ((style & SWT.VERTICAL) != 0) bits |= OS.PBS_VERTICAL;
	if ((style & SWT.INDETERMINATE) != 0) bits |= OS.PBS_MARQUEE;
	return bits;
}
*/

String windowClass () {
//	return ProgressBarClass;
	return "DIV";
}

/*
int windowProc () {
	return ProgressBarProc;
}

LRESULT WM_GETDLGCODE (int wParam, int lParam) {
	LRESULT result = super.WM_GETDLGCODE (wParam, lParam);
	if (result != null) return result;
	/*
	* Feature in Windows.  The progress bar does
	* not implement WM_GETDLGCODE.  As a result,
	* a progress bar takes focus and takes part
	* in tab traversal.  This behavior, while
	* unspecified, is unwanted.  The fix is to
	* implement WM_GETDLGCODE to behave like a
	* STATIC control.
	*-/
	return new LRESULT (OS.DLGC_STATIC);
}

LRESULT WM_SIZE (int wParam, int lParam) {
	LRESULT result = super.WM_SIZE (wParam, lParam);
	if (result != null) return result;
	/*
	* Feature in Windows.  When a progress bar with the style
	* PBS_MARQUEE becomes too small, the animation (currently
	* a small bar moving from right to left) does not have
	* enough space to draw.  The result is that the progress
	* bar does not appear to be moving.  The fix is to detect
	* this case, clear the PBS_MARQUEE style and emulate the
	* animation using PBM_STEPIT.
	* 
	* NOTE:  This only happens on Window XP.
	*-/
	if ((style & SWT.INDETERMINATE) != 0) {
		if (OS.COMCTL32_MAJOR >= 6) {
			forceResize ();
			RECT rect = new RECT ();
			OS.GetClientRect (handle, rect);
			int oldBits = OS.GetWindowLong (handle, OS.GWL_STYLE);
			int newBits = oldBits;
			if (rect.right - rect.left < MINIMUM_WIDTH) {
				newBits &= ~OS.PBS_MARQUEE;
			} else {
				newBits |= OS.PBS_MARQUEE;
			}
			if (newBits != oldBits) {
				stopTimer ();
				OS.SetWindowLong (handle, OS.GWL_STYLE, newBits);
				startTimer ();
			}
		}
	}
	return result;
}

LRESULT WM_TIMER (int wParam, int lParam) {
	LRESULT result = super.WM_TIMER (wParam, lParam);
	if (result != null) return result;
	if ((style & SWT.INDETERMINATE) != 0) {
		int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
		if (OS.COMCTL32_MAJOR < 6 || (bits & OS.PBS_MARQUEE) == 0) {
			if (wParam == TIMER_ID) {
				OS.SendMessage (handle, OS.PBM_STEPIT, 0, 0);
			}
		}
	}
	return result;
}
*/

}