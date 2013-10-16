/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.resource;

import java.util.ListResourceBundle;

public class StringResource_ja extends ListResourceBundle {
    static final Object[][] strings = { 
        { "Title", "折紙展開図エディタ ORIPA" }, 
        { "File", "ファイル" }, 
        { "Edit", "編集" }, 
        { "Help", "ヘルプ" }, 
        { "New", "新規作成" }, 
        { "Open", "開く" }, 
        { "Save", "上書き保存" }, 
        { "SaveAs", "名前を付けて保存 ..." }, 
        { "SaveAs", "画像として保存 ..." }, 
        { "ExportDXF", "エクスポート（DXF形式）" }, 
        { "EditContour", "紙の形を変更する" }, 
        { "Property", "作品情報" }, 
        { "Exit", "終了" }, 
        { "Undo", "元に戻す" }, 
        { "About", "情報表示" }, 
        { "Direction_DirectV", "入力する線分の2つの端点を指定してください。+[Ctrl]で線分上の任意の点を指定できます。" }, 
        { "Direction_OnV", "入力する線分の乗る2点を指定してください。" }, 
        { "Direction_Symmetric", "3点を指定して対称な線分を入力します。1,2点めで対称位置にある線分、2,3点めで基準となる線分を指定してください。+[Ctrl]で自動継続。" }, 
        { "Direction_TriangleSplit", "三角形の内心に向かう3つの線分を入力します。三角形の頂点を3点指定してください。" }, 
        { "Direction_Bisector", "角の2等分線を入力します。角を構成する3点を指定し、続いて線分の端点が乗る線分を指定してください。" }, 
        { "Direction_VerticalLine", "垂線を入力します。垂線の端点を指定し、続いて垂線を降ろす線分を指定してください。" }, 
        { "Direction_Mirror", "ミラーコピーする線分をクリックで選択し（再クリックで選択解除）、最後に基準とする線分を[Ctrl]+クリックしてください。" }, 
        { "Direction_ByValue", "長さと角度の値を入力し、線分の始点となる点を指定してください。値を画面から取得するには「計測」ボタンを押してください。" }, 
        { "Direction_PickLength", "2点間の長さを計測して取得します。2点を指定してください。" }, 
        { "Direction_PickAngle", "角度を計測して取得します。角を構成する3点を指定してください。" }, 
        { "Direction_ChangeLineType", "線の種類を変更する線分をクリックしてください。" }, 
        { "Direction_DeleteLine", "削除する線分を指定してください。" }, 
        { "Direction_AddVertex", "折り線上の任意の位置に頂点を追加できます。追加する場所をクリックしてください。" }, 
        { "Direction_DeleteVertex", "不要な頂点を削除できます。（注）折り線の構成に影響を与える頂点は削除できません。" }, 
        { "Direction_EditContour", "紙の輪郭形状を変更します。左クリックで入力。再クリックで終了。凸多角形でないと推定が失敗する場合があります。" }, 
        { "DefaultFileName", "無題" }, 
        { "DialogTitle_FileSave", "ファイルの保存" }, 
        { "Error_FileSaveFailed", "ファイルの保存に失敗しました" }, 
        { "Error_FileLoadFailed", "ファイルの読み込みに失敗しました" }, 
        { "Warning_SameNameFileExist", "同じ名前のファイルが存在します。上書きしますか？" }, 
        { "ORIPA_File", "折紙展開図エディタファイル" },       
        { "Picture_File", "画像ファイル" }, 
        { "UI_InputLine", "線入力" }, 
        { "UI_Select", "選択" }, 
        { "UI_DeleteLine", "線削除" }, 
        { "UI_ShowGrid", "グリッドを表示する" }, 
        { "UI_ChangeLineType", "線種変更" }, 
        { "UI_AddVertex", "頂点追加" }, 
        { "UI_DeleteVertex", "頂点削除" }, 
        { "UI_Mesure", "計測" }, 
        { "UI_Fold", "折りたたみ推定..." }, 
        { "UI_GridSizeChange", "変更" }, 
        { "UI_ShowVertices", "頂点を表示する" }, 
        { "UI_EditMode", "編集モード" }, 
        { "UI_LineInputMode", "折り線の指定方法"},
        { "UI_Length", "長さ"},
        { "UI_Angle", "角度"},
        { "UI_GridDivideNum", "分割数"},
        { "Warning_foldFail1", "失敗しました。重複した線分の自動削除を行って再試行しますか？"},
        { "Warning_foldFail2", "展開図に基本的な問題点があるようです"},
        { "MENU_Disp", "表示" },
        { "MENU_ExportModelLine_DXF", "輪郭線のエクスポート（DXF形式）" },
        { "MENU_Invert", "反転する" },
        { "MENU_SlideFaces", "重なり部をずらして表示する" },
        { "Direction_Basic", "    L: 回転 R:移動 Wheel:ズーム " },
        { "MENU_DispType", "塗りつぶし方法" },
        { "MENU_FillColor", "面の塗りつぶし（カラー）:正しくない場合あり" },
        { "MENU_FillWhite", "面の塗りつぶし（ホワイト）:正しくない場合あり" },
        { "MENU_FillAlpha", "面の塗りつぶし（透過）" },
        { "MENU_DrawLines", "輪郭のみ" },
        { "ExpectedFoldedOrigami", "折紙完成予想図" }
    };
    
    @Override
    protected Object[][] getContents() {
        return strings;
    }
}
